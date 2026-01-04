import { Notification, Warning2 } from "iconsax-react";
import RevenueChart from "../components/BarChart";
import Calenda from "../components/Calenda";

const Home = () => {
  return (
    <>
      <div className="flex flex-col w-full min-w-0 bg-w">
        <div className="flex mt-5 gap-4 flex-wrap">
          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <Notification size="25" color="#000"/>
                </div>
                <div className="text-xl font-bold"> Quantity user</div>
              </div>
              <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <Warning2 size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">129</span>
                <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1">tang</span>
              </div>
              <div className="text-xl text-gray-400">
                vs last week
              </div>
            </div>
          </div>

          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <Notification size="25" color="#000"/>
                </div>
                <div className="text-xl font-bold"> Quantity user</div>
              </div>
              <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <Warning2 size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">129</span>
                <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1">tang</span>
              </div>
              <div className="text-xl text-gray-400">
                vs last week
              </div>
            </div>
          </div>


          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <Notification size="25" color="#000"/>
                </div>
                <div className="text-xl font-bold"> Quantity user</div>
              </div>
              <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <Warning2 size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">129</span>
                <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1">tang</span>
              </div>
              <div className="text-xl text-gray-400">
                vs last week
              </div>
            </div>
          </div>

          <div className="flex-1 min-w-[200px] h-50 bg-white rounded-xl border border-gray-300 shadow-md p-10">
            <div className="flex justify-between items-center">
              <div className="flex gap-4">
                <div className="size-10 bg-white border border-gray-200 shadow-md rounded-md p-1">
                  <Notification size="25" color="#000"/>
                </div>
                <div className="text-xl font-bold"> Quantity user</div>
              </div>
              <div className="size-8 bg-white border border-gray-200 shadow-md p-1 rounded-2xl">
                <Warning2 size="20" color="#000"/>
              </div>
            </div>
            <div className="flex gap-2 items-center justify-between mt-5">
              <div className="flex items-center">
                <span className="text-5xl font-bold mr-2">129</span>
                <span className="w-15 h-8 border rounded-md border-gray-200 bg-green-100 text-green-500 p-1">tang</span>
              </div>
              <div className="text-xl text-gray-400">
                vs last week
              </div>
            </div>
          </div>
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
