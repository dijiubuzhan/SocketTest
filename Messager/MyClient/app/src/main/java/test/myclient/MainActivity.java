package test.myclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MessengerActivity";
    private TextView tv_process;
    private Messenger mSendMsg;
    private Messenger mReplyMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_process = (TextView) findViewById(R.id.tv_process);
        Button btn_messenger_start = (Button) findViewById(R.id.btn_messenger_start);
        btn_messenger_start.setOnClickListener(this);

        mReplyMsg = new Messenger(mClientHandler);
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mMessageConn, Context.BIND_AUTO_CREATE);
    }

    Handler mClientHandler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            tv_process.setText(bundle.getString("msg"));
        }
    };


    private ServiceConnection mMessageConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mSendMsg = new Messenger(binder);
        }

        public void onServiceDisconnected(ComponentName name) {
        }

    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_messenger_start) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", "aaa");
            Message msg = Message.obtain();
            msg.setData(bundle);
            msg.replyTo = mReplyMsg;
            try {
                mSendMsg.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
