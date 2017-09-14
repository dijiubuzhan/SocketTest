package com.test.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    private static final String TAG ="TCPServiceShan" ;
    private boolean mIsServerDestroyed=false;
    private String[] mDefinedMessages=new String[]{"aaaaaaa","哈哈哈哈哈哈","哎呦喂","你是不是傻","你瞅啥","瞅你咋地"};
    public TCPServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mIsServerDestroyed = true;
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        System.out.println("TCPServerService start");
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    private class TcpServer implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket=null;
            try {
                serverSocket=new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server failed,port:8688");
                Log.d(TAG, "run: IOException e11122="+e.getMessage());
                e.printStackTrace();
                return;
            }

            while (!mIsServerDestroyed){

                try {
                    Log.d(TAG, "run: recycle,mIsServerDestroyed="+mIsServerDestroyed);
                    final Socket client=serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                Log.d(TAG, "run: IOException exxxxx="+e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    Log.d(TAG, "run: IOException e="+e.getMessage());
                    e.printStackTrace();
                }

            }
        }

    }

    private void responseClient(Socket client) throws IOException{
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));

        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("欢迎来到聊天室！");
        while (!mIsServerDestroyed){
            String str=in.readLine();
            System.out.println("msg from client:"+str);
            if(str==null){
                break;
            }
            int i=new Random().nextInt(mDefinedMessages.length);
            String msg=mDefinedMessages[i];
            out.println(msg);
            System.out.println("send:"+msg);
        }
        System.out.println("client quit.");
        // 关闭流
        MyUtils.close(out);
        MyUtils.close(in);
        client.close();
    }

}
