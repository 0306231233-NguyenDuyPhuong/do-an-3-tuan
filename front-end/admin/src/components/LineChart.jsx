import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from "recharts";

function UserChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" type="category"/>
        <YAxis />
        <Tooltip />
        <Line type="monotone" dataKey="count" stroke="#6F45E6" />
      </LineChart>
    </ResponsiveContainer>
  );
}

export default UserChart;
