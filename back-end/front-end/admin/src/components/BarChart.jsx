import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const data = [
  { name: "Jan", revenue: 1200 },
  { name: "Feb", revenue: 1900 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Jan", revenue: 1200 },
  { name: "Feb", revenue: 1900 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
  { name: "Mar", revenue: 3000 },
];

export default function Chart() {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data}>
        <XAxis dataKey="name" />
        <YAxis />
        <Tooltip />
        <Bar dataKey="revenue"      fill="#6F45E6"
      radius={[6, 6, 0, 0]}
      activeBar={{ fill: "#5B34D6" }}
   />
      </BarChart>
    </ResponsiveContainer>
  );
}
