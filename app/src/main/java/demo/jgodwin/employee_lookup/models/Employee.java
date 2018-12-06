package demo.jgodwin.employee_lookup.models;

import java.io.Serializable;

public class Employee implements Serializable {
    //rest service json example
    // {"id":"1","employee_name":"","employee_salary":"0","employee_age":"0","profile_image":""}

    public int id;
    public String employee_name;
    public float employee_salary;
    public int employee_age; // will age come back with decimals? like 24.6 years old?
    public String profile_image;
}
