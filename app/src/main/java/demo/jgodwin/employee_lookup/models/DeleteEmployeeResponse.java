package demo.jgodwin.employee_lookup.models;

public class DeleteEmployeeResponse {
    // this response the restful api gives is a bit awkward but i wanted classes to deserialize straight in to.
    //{"success":{"text":"successfully! deleted Records"}}
    public DeleteEmployeeText success = new DeleteEmployeeText();
}

