package demo.jgodwin.employee_lookup.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.Consumer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import demo.jgodwin.employee_lookup.R;
import demo.jgodwin.employee_lookup.models.Employee;
import demo.jgodwin.employee_lookup.tasks.DownloadImageTask;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeViewHolder>{

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAge, textViewName, textViewSalary;
        public ImageView imageViewProfile;

        public EmployeeViewHolder(View view ) {
            super(view);

            imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfile);
            textViewAge = (TextView) view.findViewById(R.id.textViewAge);
            textViewName = (TextView) view.findViewById(R.id.textViewName);
            textViewSalary = (TextView) view.findViewById(R.id.textViewSalary);
        }
    }

    private List<Employee> employeeData;
    private Consumer<Employee> employeeSelectedCallback;

    public EmployeeListAdapter(List<Employee> data, @Nullable Consumer<Employee> employeeSelectedCallback) {

        employeeData = data;
        this.employeeSelectedCallback = employeeSelectedCallback;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.employee_card, viewGroup, false);

        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder employeeViewHolder, int position) {
        Employee employee = employeeData.get(position);

        employeeViewHolder.textViewAge.setText(String.valueOf(employee.employee_age));
        employeeViewHolder.textViewName.setText(employee.employee_name);
        employeeViewHolder.textViewSalary.setText(String.valueOf(employee.employee_salary));
        employeeViewHolder.itemView.setTag(employee);

        employeeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(employeeSelectedCallback != null) {
                    Employee emp = (Employee) v.getTag();
                    employeeSelectedCallback.accept(emp);
                }
            }
        });

        String profileUrl = "https://randomuser.me/api/portraits/med/women/52.jpg";
        //^^^^ would use employee.profile_image but the service is not returning actual urls in most of the results.
        // so for the sake of the demo I will just use this.

        //employeeViewHolder.imageViewProfile.setImageURI(Uri.parse(profileUrl));
        // i thought this might work but it seems to only be for file system resources?

        DownloadImageTask task = new DownloadImageTask(employeeViewHolder.imageViewProfile);
        task.execute(profileUrl);
    }

    @Override
    public int getItemCount() {

        if(employeeData == null){
            return 0;
        }
        return employeeData.size();
    }

}
