package edu.hitsz;

public class feiwu {
    public static void main(String[] args) {
        //                    if ("close".equals(key)) {
//                        //
//                        if (out != null) {
//                            try {
//                                out.close();
//                                socket.close();
//                                in.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else if ("add".equals(key)) {
//                        PlayerRecord record = (PlayerRecord) jsonObject.get("add");
//                        MyDB4records.createNewAccount(record);
//                    } else if ("deleteByName".equals(key)) {
//                        String deleteName = jsonObject.getString("deleteByName");
//                        MyDB4records.deleteByName(deleteName);
//                    } else if ("getAll".equals(key)) {
//                        int diff = jsonObject.getInt("getAll");
//                        ArrayList<PlayerRecord> allUser = MyDB4records.getAllUser(diff);
//                        if (socket.isConnected()) {
//                            try {
//                                out = new ObjectOutputStream(socket.getOutputStream());
//                                out.writeObject(allUser);
//                                System.out.println("传给客户端 " + allUser);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
    }
}
