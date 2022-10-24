import React from 'react';
import WeeklyCalenderHeader from './WeeklyCalendarHeader';
import WeeklyCalendarBody from './WeeklyCalendarBody';

const WeeklyCalendar = () => {
  return (
    <section className="w-full flex flex-col m-2">
      <div className="flex flex-col basis-9/12 min-w-[500px]">
        <WeeklyCalenderHeader />
        <WeeklyCalendarBody />
      </div>
    </section>
  );
};

export default WeeklyCalendar;