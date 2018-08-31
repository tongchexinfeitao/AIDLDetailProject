
package com.ali.aidlclient;

import com.ali.aidlclient.Student;

interface IStudentManager {

   List<Student> getAllStudents();
   void  joinStudent(inout Student student);

}
