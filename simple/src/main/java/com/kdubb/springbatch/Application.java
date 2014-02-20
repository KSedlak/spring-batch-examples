package com.kdubb.springbatch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.kdubb.springbatch.model.Person;

@ComponentScan
public class Application {
    public static void main(String[] args) {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(BatchConfiguration.class);

    	executeJob(ctx);

        List<Person> results = ctx.getBean(JdbcTemplate.class).query("SELECT first_name, last_name FROM people", new RowMapper<Person>() {
            @Override
            public Person mapRow(ResultSet rs, int row) throws SQLException {
            	Person person = new Person();
            	person.setFirstName(rs.getString(1));
            	person.setLastName(rs.getString(2));
            	
                return person;
            }
        });

        for (Person person : results) {
            System.out.println("Found <" + person + "> in the database.");
        }
    }
    
    private static void executeJob(ApplicationContext ctx) {
    	Job job = ctx.getBean(Job.class);
    	JobLauncher launcher = ctx.getBean(JobLauncher.class);
    	
		try {
			JobParameters parameters = new JobParameters();
			launcher.run(job, parameters);
		}
		catch(JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			e.printStackTrace();
		}
    }
}