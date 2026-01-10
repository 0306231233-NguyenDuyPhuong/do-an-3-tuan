import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from "recharts";

function ReportByTypeBar({ data }) {
  if (!data || data.length === 0) return <p>No data</p>;

  return (
    <ResponsiveContainer width="100%" height="100%">
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="target_type" />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Bar dataKey="count" fill="#6F45E6" />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default ReportByTypeBar;
