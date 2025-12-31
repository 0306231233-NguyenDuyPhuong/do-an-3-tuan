import RevenueChart from "../components/BarChart";
import Calenda from "../components/Calenda";

const Home = () => {
  return (
    <>
      <div className="flex flex-col w-full min-w-0 bg-w">

        <div className="flex mt-5 gap-4 flex-wrap">
          <div className="flex-1 min-w-[200px] h-60 bg-white rounded-xl border border-gray-300 border-2 shadow-md"></div>
          <div className="flex-1 min-w-[200px] h-60 bg-white rounded-xl border border-gray-300 border-2 shadow-md"></div>
          <div className="flex-1 min-w-[200px] h-60 bg-white rounded-xl border border-gray-300 border-2 shadow-md"></div>
          <div className="flex-1 min-w-[200px] h-60 bg-white rounded-xl border border-gray-300 border-2 shadow-md"></div>
        </div>

        <div className="flex gap-5 mt-10">
          <div className="flex-3 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <div className="flex justify-between">
              <div className="text-black font-bold text-xl">Revenue</div>
              <div className="text-black font-bold text-xl">Revenue</div>
            </div>
            <RevenueChart/>
          </div>
          <div className="flex-1 h-100 bg-white rounded-xl border border-gray-300 border-2 shadow-md p-10">
            <Calenda/>
          </div>
        </div>
      </div>
    </>
  );
};

export default Home;
