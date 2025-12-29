import RevenueChart from "../components/BarChart";

const Home = () => {
  return (
    <>
      {/* 🔧 SỬA 1: size-full → w-full */}
      <div className="flex flex-col w-full min-w-0">

        <div>
          <span className="text-black font-bold text-2xl">
            Dashboard
          </span>
        </div>

        {/* 🔧 SỬA 2: thêm flex-wrap */}
        <div className="flex mt-5 gap-4 flex-wrap">

          {/* 🔧 SỬA 3: w-100 → flex-1 + min-w */}
          <div className="flex-1 min-w-[220px] h-40 bg-white rounded-xl p-5">
            <div className="flex flex-col gap-3">
              <div className="size-10 bg-blue-200 rounded-full"></div>
              <div>item2</div>
            </div>
          </div>

          <div className="flex-1 min-w-[220px] h-40 bg-white rounded-xl"></div>
          <div className="flex-1 min-w-[220px] h-40 bg-white rounded-xl"></div>
          <div className="flex-1 min-w-[220px] h-40 bg-white rounded-xl"></div>

        </div>

        {/* Chart */}
        <div className="bg-white p-5 rounded-lg shadow mt-10">
          <h2 className="mb-4 text-lg font-semibold">
            Doanh thu theo tháng
          </h2>

          <RevenueChart />
        </div>

        <div>item4</div>
      </div>
    </>
  );
};

export default Home;
