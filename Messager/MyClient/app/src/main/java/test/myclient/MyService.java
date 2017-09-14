package test.myclient;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MyService extends Service {
    private static final String TAG = "MessageService";
    private Messenger mRecvMsg;
    private Messenger mReplyMsg;
    private Bundle mBundle;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mRecvMsg.getBinder();
    }

    Handler mServerHandler = new Handler(){
        public void handleMessage(Message msg) {
            mReplyMsg = msg.replyTo;
            mBundle = msg.getData();
            new ReplyThread().start();
        }
    };


    class ReplyThread extends Thread {
        public void run() {
            String desc = String.format("请求参数为%s，应答参数为%s",
                    mBundle.getString("msg"), "bbb");
            Bundle bundle = new Bundle();
            bundle.putString("msg", desc);
            Message msg = Message.obtain();
            msg.setData(bundle);
            try {
                mReplyMsg.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCreate() {
        mRecvMsg = new Messenger(mServerHandler);
        super.onCreate();
    }


}
