package com.ali.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ali.aidlclient.IStudentManager;
import com.ali.aidlclient.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mumu on 2018/8/30.
 *  需要在功能清单中声明action
 */

public class StudentManagerService extends Service {


    //服务端存储数据的集合
    List<Student> studentList = new ArrayList<>();

    //服务端AIDL接口Stub子类的实例
    IStudentManager.Stub stub = new IStudentManager.Stub() {
        @Override
        public List<Student> getAllStudents() throws RemoteException {
            Log.e("tag", "service  ====   getALLStudents() " + studentList.toString());
            return studentList;
        }

        @Override
        public void joinStudent(Student student) throws RemoteException {
            Log.e("tag", "service ====   joinStudent() " + student.toString());
            Log.e("tag", "service ====   joinStudent() 服务端修改age为180000");
            student.setAge(180000);
            studentList.add(student);
        }
    };

    /**
     * 返回IBinder实例（同时也是AIDL接口的实现类）
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("tag", "service ====   onBind()");
        return stub;
    }

}
