package com.github.elenterius.biomancy.util;

import java.util.Calendar;
import java.util.Date;

public class EventCalendar {

	private final Calendar calendar;

	private boolean isAprilFools = false;
	private boolean isCarnivalStart = false;

	public EventCalendar() {
		calendar = Calendar.getInstance();
		update();
	}

	public void update() {
		calendar.setTime(new Date());

		isAprilFools = calendar.get(Calendar.MONTH) == Calendar.APRIL
				&& calendar.get(Calendar.DATE) == 1;

		isCarnivalStart = calendar.get(Calendar.MONTH) == Calendar.NOVEMBER
				&& calendar.get(Calendar.DATE) == 11
				&& calendar.get(Calendar.HOUR_OF_DAY) >= 11
				&& calendar.get(Calendar.MINUTE) >= 11;
	}

	public boolean isAprilFools() {
		return isAprilFools;
	}

	public boolean isCarnivalStart() {
		return isCarnivalStart;
	}

}
