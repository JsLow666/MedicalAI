package com.junsheng.XiaozhiAI.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junsheng.XiaozhiAI.bean.Appointment;

public interface AppointmentService extends IService<Appointment> {
    Appointment getOne(Appointment appointment);
}