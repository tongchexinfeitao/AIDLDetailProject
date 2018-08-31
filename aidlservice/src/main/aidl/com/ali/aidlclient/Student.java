package com.ali.aidlclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mumu on 2018/8/30.
 */

public class Student implements Parcelable {
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.name);
    }

    /**
     * AIDL支持 out 流向的时候，必须手写  readFromParcel(Parcel parcel)方法
     * @param parcel
     */
    public void readFromParcel(Parcel parcel) {
        age = parcel.readInt();
        name = parcel.readString();
    }



    public Student(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Student() {
    }

    protected Student(Parcel in) {
        this.age = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
