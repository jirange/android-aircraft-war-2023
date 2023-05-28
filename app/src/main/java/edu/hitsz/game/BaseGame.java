package edu.hitsz.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.GameActivity;
import edu.hitsz.activity.LoginActivity;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemy.factory.BossEnemyFactory;
import edu.hitsz.aircraft.enemy.factory.EnemyFactory;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.music.MySoundPool;
import edu.hitsz.network.UserClientThread;
import edu.hitsz.observer.Subscriber;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.strategy.shoot.ScatteringShoot;

/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 *
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private UserClientThread clientThread;
    private Handler clientThreadHandler;
    private int matchScore = 0;
    private boolean matchDead = false;


    public static final String TAG = "BaseGame";
    boolean mbLoop = false; //控制绘画线程的标志位
    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas;  //绘图的画布
    private Paint mPaint;
    private Handler handler;

    //点击屏幕位置
    float clickX = 0, clickY = 0;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;


    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 16;

    private final HeroAircraft heroAircraft;
    private AbstractEnemyAircraft bossEnemy;

    protected final List<AbstractEnemyAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseProp> props;


    /**
     * 游戏难度相关参数
     * 普通敌机、精英敌机的产生分配的概率
     */
    protected int enemyMaxNumber = 5;//2;
    protected double mobEnemyPro = 0.7;
    protected double superEnemyPro = 1 - mobEnemyPro;

    /**
     * boss敌机相关参数
     * scoreToBoss 生成BOSS敌机的阈值
     */
    private int scoreToBoss = 100;
    protected int bossHp = 150;
    protected int bossHpAdd = 0;
    protected int borderAddForBoss = 0;


    private boolean gameOverFlag = false;
    private int score = 0;

    public int getTime() {
        return time;
    }

    private int time = 0;


    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    protected int cycleDuration = 600;
    protected int cycleTime = 0;

    public BaseGame(Context context, Handler handler) {
        super(context);
        this.handler = handler;
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        ImageManager.initImage(context);

        // 初始化英雄机
        if (HeroAircraft.getHeroAircraft() != null) {
            HeroAircraft.setHeroAircraftNull();
        }

        heroAircraft = HeroAircraft.getHeroAircraft();
        heroAircraft.setHp(1000);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();


        heroController();
        if (GameActivity.online) {
            clientThreadHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //如果消息来自于子线程  clientThread  即数据库操作的返回值
                    if (msg.what == 0x103) {
                        System.out.println("matcher score" + msg.obj);
                        if (msg.obj == null) {
                            matchScore = 0;
                        } else {
                            matchScore = Integer.valueOf((String) msg.obj);
                            if (matchScore < -1) {
                                System.out.println("对方死亡");
                                matchDead = true;
                                matchScore = -matchScore;
                            } else if (matchScore == -1) {
                                System.out.println("对方死亡");
                                matchDead = true;
                                matchScore = 0;
                            }
                        }
                    }
                }
            };
            clientThread = UserClientThread.getClientThread(clientThreadHandler);
        }

        //避免下一句的时候还是true
        matchDead = false;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    Log.d("BaseGame", "produceEnemy");

                    AbstractEnemyAircraft enemy = getEnemyAircraft();
                    enemyAircrafts.add(enemy);
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 道具移动
            propsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            try {
                crashCheckAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //BOSS敌机生成
            creatBossEnemy();


            // 后处理
            postProcessAction();

            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //}
        };
        new Thread(task).start();
    }

    public void heroController() {
        setOnTouchListener((view, motionEvent) -> {
            clickX = motionEvent.getX();
            clickY = motionEvent.getY();
            heroAircraft.setLocation(clickX, clickY);

            // 防止超出边界
            return !(clickX < 0) && !(clickX > MainActivity.screenWidth) && !(clickY < 0) && !(clickY > MainActivity.screenHeight);
        });
    }


    private void shootAction() {
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    private boolean timeCountAndNewCycleJudge() {
        // 控制游戏难度
        controlDifficulty();

        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 控制游戏难度 如周期，最大敌机数量，普通敌机概率，精英敌机概率，敌机血量增，敌机速度增幅
     * cycleDuration,enemyMaxNumber,mobEnemyPro,superEnemyPro, enemyHpAdd, enemySpeedAdd
     */
    protected abstract void controlDifficulty();

    /**
     * 监听 创建Boss敌机对象
     */
    public void creatBossEnemy() {
        synchronized (this) {
            EnemyFactory factory;
            // 分数达到设定阈值后出现BOSS敌机，可多次出现
            if (score >= scoreToBoss) {
                if (bossEnemy == null || bossEnemy.notValid()) {
                    factory = new BossEnemyFactory();
                    bossEnemy = factory.createEnemy(5, bossHp);
                    bossHp += bossHpAdd;
                    System.out.println("boss敌机血量为" + bossEnemy.getHp());
                    //设置为散射弹道
                    bossEnemy.setShootStrategy(new ScatteringShoot());
                    enemyAircrafts.add(bossEnemy);
                    scoreToBoss += borderAddForBoss;
                }
            }
        }
    }

    /**
     * 生成普通敌机
     * 简单模式 难度不变 普通和困难模式 难度要变
     *
     * @return 敌机对象
     */
    protected abstract AbstractEnemyAircraft getEnemyAircraft();

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }


    private void propsMoveAction() {
        for (BaseProp prop : props) {
            prop.forward();
        }
    }


    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() throws InterruptedException {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                //todo 加载被击中音效
                MySoundPool.playMusic("bullet_hit");
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    //todo 加载被击中音效
                    MySoundPool.playMusic("bullet_hit");

                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());

                    bullet.vanish();
                    if (enemyAircraft.notValid()) {


                        // TODO 获得分数，产生道具补给
                        //普通敌机 坠毁+10分  精英敌机+20  boss敌机+80
                        score += enemyAircraft.getCrashScore();
                        //产生道具补给   敌机坠毁后，以一定概率随机掉落某种道具
                        List<BaseProp> baseProps = enemyAircraft.dropProp();
                        if (baseProps != null) {
                            props.addAll(baseProps);
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得补给
        for (BaseProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                // 将英雄机加入炸弹观察者清单
                if (prop instanceof BombProp) {
                    ArrayList<Subscriber> subscribers = new ArrayList<>();
                    subscribers.addAll(enemyBullets);
                    subscribers.addAll(enemyAircrafts);
                    subscribers.add(heroAircraft);
                    ((BombProp) prop).setSubscribers(subscribers);
                    for (AbstractEnemyAircraft enemy : enemyAircrafts) {
                        score += enemy.getCrashScore();
                    }
                }
                // 英雄碰到道具
                // 道具生效
                // 道具生效音效
                MySoundPool.playMusic("get_supply");

                System.out.println(prop.getSpeedY());
                prop.activeProp(heroAircraft);
                prop.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     * 4. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);

        if (heroAircraft.notValid()) {
            //让boss无效
            if (bossEnemy != null && !bossEnemy.notValid()) {
                bossEnemy.vanish();
            }
            // 游戏结束音效响起来
            MySoundPool.playMusic("game_over");

            gameOverFlag = true;

            mbLoop = false;
            Log.i(TAG, "heroAircraft is not Valid");
        }

    }

    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if (mSurfaceHolder == null || canvas == null) {
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround, 0, this.backGroundTop - backGround.getHeight(), mPaint);
        canvas.drawBitmap(backGround, 0, this.backGroundTop, mPaint);
        backGroundTop += 1;
        if (backGroundTop == MainActivity.screenHeight)
            this.backGroundTop = 0;

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹


        paintImageWithPositionRevised(heroBullets);  //英雄机子弹


        paintImageWithPositionRevised(props);//道具

        paintImageWithPositionRevised(enemyAircrafts);//敌机


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值
        paintScoreAndLife();

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(List<? extends AbstractFlyingObject> objects) {
//        if (objects.size() == 0) {
//            return;                //为了防止ConcurrentModificationException 所以删了
//        }
//
//        for (AbstractFlyingObject object : objects) {
//            Bitmap image = object.getImage();
//            assert image != null : objects.getClass().getName() + " has no image! ";
//            canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
//                    object.getLocationY() - image.getHeight() / 2, mPaint);
//        }
        if (objects != null && objects.size() == 0) {
            return;
        } else {
            for (int i = 0; i < objects.size(); i++) {
                AbstractFlyingObject object = objects.get(i);
                Bitmap image = object.getImage();
                assert image != null : objects.getClass().getName() + " has no image! ";
                canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                        object.getLocationY() - image.getHeight() / 2, mPaint);
            }
        }

    }

    private void paintScoreAndLife() {
        int x = 10;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
//        if (gameOverFlag) {
//            mPaint.setColor(Color.GRAY);
//        }
        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
        if (GameActivity.online) {
            y = y + 60;
            if (!matchDead) {
                canvas.drawText("Matcher SCORE:" + this.matchScore, x, y, mPaint);
            } else {
                mPaint.setColor(Color.GRAY);
                canvas.drawText("Matcher SCORE:" + this.matchScore, x, y, mPaint);
            }

            if (gameOverFlag) {
                mPaint.setTextSize(100);
                mPaint.setColor(Color.GRAY);

                canvas.drawText("GAME OVER", MainActivity.screenWidth / 2, MainActivity.screenHeight / 2, mPaint);
            }
        }

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread(this).start();
        Log.i(TAG, "start surface view thread");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        MainActivity.screenWidth = i1;
        MainActivity.screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mbLoop = false;
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (mbLoop) {   //游戏结束停止绘制
                action();
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (GameActivity.online && !matchDead) {//对手死后还向对方发送分数吗？  暂且不发送了
            new Thread(() -> {
                while (mbLoop) {   //游戏结束停止绘制
                    Message msg;
                    msg = new Message();
                    msg.what = 0x111;
                    msg.obj = score;
                    while (true) {
                        if (clientThread.toserverHandler != null) break;
                    }
                    clientThread.toserverHandler.sendMessage(msg);//具体信息

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        while (mbLoop) {   //游戏结束停止绘制
            synchronized (mSurfaceHolder) {
                draw();
            }
        }


        Message msg;
        msg = new Message();
        msg.what = 0x111;
        if (score == 0) {
            msg.obj = -1;

        } else {
            msg.obj = -score;
        }
        while (true) {
            if (clientThread.toserverHandler != null) break;
        }
        clientThread.toserverHandler.sendMessage(msg);//具体信息

        while (!matchDead) {
            synchronized (mSurfaceHolder) {
                // TODO: 2023/5/28 需要重复绘制吗 可以试一试
                deadDraw();

            }
        }

        if (matchDead) {
            Message message = Message.obtain();
            message.what = 1;
            message.obj = score;
            handler.sendMessage(message);
        }
    }

    private void deadDraw() {
        //将ImageView变成灰色
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mPaint.setColorFilter(filter);

        canvas = mSurfaceHolder.lockCanvas();
        if (mSurfaceHolder == null || canvas == null) {
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround, 0, MainActivity.screenHeight, mPaint);
        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹

        paintImageWithPositionRevised(heroBullets);  //英雄机子弹


        paintImageWithPositionRevised(props);//道具

        paintImageWithPositionRevised(enemyAircrafts);//敌机


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值
        paintScoreAndLife();

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

}
