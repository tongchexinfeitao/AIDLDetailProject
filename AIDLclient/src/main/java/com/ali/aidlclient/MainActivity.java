package com.ali.aidlclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    //AIDL接口
    private IStudentManager iStudentManager;
    //连接状态
    boolean isConnected = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //得到AIDL接口
            iStudentManager = IStudentManager.Stub.asInterface(service);
            isConnected = true;
            Log.e("tag", "client ====   onServiceConnected() 绑定成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("tag", "client ====   onServiceDisconnected() 解绑");
            isConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * 绑定服务端 （如果只需要在界面可见的时候使用服务端，那就在onstart中绑定，onStop中解绑
     * 如果想长期在后台也操作的话，就在oncreate中绑定，在onDestroy中解绑）
     */
    @Override
    protected void onStart() {
        super.onStart();
        attempToConnectService();
    }


    /**
     * 解绑服务端
     */
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        Log.e("tag", "client ====   unbindService() ");
    }

    /**
     * 绑定服务端的服务，PS： 跨进程绑定服务，必须制定package才能绑定成功
     */
    public void attempToConnectService() {
        Log.e("tag", "client ====   bindService() 客户端开始绑定服务 ");
        Intent intent = new Intent();
        intent.setAction("com.ali.aidlservice.student.action");
        //必须指定Package
        intent.setPackage("com.ali.aidlservice");
        //绑定服务
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 给服务端添加一个学生，PS: 其中验证了inout双向模式，服务端修改对象后，客户端对象自动修改
     */
    public void joinStudent(View view) {
        if (iStudentManager == null) {
            Log.e("tag", "client ====   joinStudent()  客户端还未绑定服务端");
            attempToConnectService();
            return;
        }
        if (iStudentManager != null) {
            try {
                Student student = new Student(18, "wang");
                Log.e("tag", "client ====   joinStudent()  客户端 " + student.toString());
                iStudentManager.joinStudent(student);
                Log.e("tag", "client ====   joinStudent() 服务端修改student之后，客户端的值" + student.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端调用服务端方法，拿所有的学生
     */
    public void getAllStudent(View view) {
        //当没有绑定的时候，绑定服务
        if (iStudentManager == null) {
            Log.e("tag", "client ====   joinStudent()  客户端还未绑定服务端");
            attempToConnectService();
            return;
        }
        if (iStudentManager != null) {
            try {
                Log.e("tag", "client  ====   getALLStudents()  客户端调用");
                List<Student> allStudents = iStudentManager.getAllStudents();
                Log.e("tag", "client  ====   getALLStudents()  客户端拿到结果" + allStudents.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
