package com.roodie.model.entities;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

/**
 * Created by Roodie on 07.07.2015.
 */
public class CreditWrapper implements Comparable<CreditWrapper> {

    private PersonWrapper person;
    private String job;
    private String department;
    private int order;

    private static int ORDER_DIRECTOR = 0;
    private static int ORDER_WRITER = 1;
    private static int ORDER_PRODUCER = 2;
    private static int ORDER_PRODUCTION = 3;
    private static int ORDER_EDITING = 4;
    private static int ORDER_CAMERA = 5;
    private static int ORDER_ART = 6;
    private static int ORDER_SOUND = 7;

    public CreditWrapper(PersonWrapper person, String job, int order) {
        this.person = Preconditions.checkNotNull(person, "Person cannot be null");
        this.job = Preconditions.checkNotNull(job, "Job cannot be null");
        this.order = order;
    }

    public CreditWrapper(PersonWrapper person, String job, String department) {
        this.person = Preconditions.checkNotNull(person, "Person cannot be null");
        this.job = Preconditions.checkNotNull(job, "Job cannot be null");
        this.department = Preconditions.checkNotNull(department, "Department cannot be null");
        this.order = crewOrderByJob(this);
    }

    public PersonWrapper getPerson() {
        return person;
    }

    public String getJob() {
        return job;
    }

    public String getDepartment() {
        return department;
    }

    private int getOrder() {
        return order;
    }

    @Override
    public int compareTo(@NonNull CreditWrapper another) {
        int thisOrder = getOrder();
        int otherOrder = another.getOrder();

        if (thisOrder != otherOrder) {
            return thisOrder - otherOrder;
        } else
        return this.person.name.compareTo(another.person.name);
    }

    private static int crewOrderByJob(CreditWrapper crew) {
        if (crew.job.equals("Director")) {
            return ORDER_DIRECTOR;
        } else if (crew.department.equals("Writing")) {
            return ORDER_WRITER;
        } else if (crew.job.equals("Producer")) {
            return ORDER_PRODUCER;
        } else if (crew.department.equals("Production")) {
            return ORDER_PRODUCTION;
        } else if (crew.department.equals("Editing")) {
            return ORDER_EDITING;
        } else if (crew.department.equals("Camera")) {
            return ORDER_CAMERA;
        } else if (crew.department.equals("Art")) {
            return ORDER_ART;
        } else if (crew.department.equals("Sound")) {
            return ORDER_SOUND;
        }
        return 69;
    }
}
