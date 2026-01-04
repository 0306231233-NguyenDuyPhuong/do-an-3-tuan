import { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

const MyCalendar = () => {
  const [value, setValue] = useState(new Date());

  return (
    <div className="w-full h-full">
      <Calendar onChange={setValue} value={value} />
      <p className="mt-2">
        Ngày đã chọn: {value.toLocaleDateString()}
      </p>
    </div>
  );
};

export default MyCalendar;
3