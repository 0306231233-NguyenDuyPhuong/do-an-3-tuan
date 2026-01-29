import { useState } from "react";
import { useUserStore } from "../stores/useUserStore";

export default function ProfilePage() {
  const { user, updateProfile } = useUserStore();

  const [form, setForm] = useState({
    full_name: user?.full_name || "",
    phone: user?.phone || "",
    gender: user?.gender ?? 1,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: name === "gender" ? Number(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await updateProfile(form);
      alert("Cập nhật hồ sơ thành công");
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Cập nhật thất bại");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-6xl mx-auto px-6 py-6 flex items-center gap-6">
          <div
            className="w-24 h-24 rounded-full bg-gray-300 overflow-hidden cursor-pointer"
            onClick={() => document.getElementById("avatarInput").click()}
          >
            <img
              src={`http://localhost:8989/api/images/${user?.avatar}`}
              className="w-full h-full object-cover"
              onError={(e) => (e.target.src = "/default-avatar.png")}
            />
          </div>


          <div>
            <h1 className="text-2xl font-semibold text-gray-800">
              {form.full_name}
            </h1>
            <p className="text-gray-500 text-md">{user?.email}</p>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-6xl mx-auto px-6 py-8">
        <form
          onSubmit={handleSubmit}
          className="bg-white rounded-xl shadow p-6 space-y-6"
        >
          <h2 className="text-lg font-semibold text-gray-700">
            Thông tin cá nhân
          </h2>

          {/* Full name */}
          <div>
            <label className="block text-sm font-medium text-gray-600">
              Họ và tên
            </label>
            <input
              name="full_name"
              value={form.full_name}
              onChange={handleChange}
              className="mt-1 w-full rounded-lg border px-4 py-2"
            />
          </div>

          {/* Phone */}
          <div>
            <label className="block text-sm font-medium text-gray-600">
              Số điện thoại
            </label>
            <input
              name="phone"
              value={form.phone}
              onChange={handleChange}
              className="mt-1 w-full rounded-lg border px-4 py-2"
            />
          </div>

          {/* Gender */}
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-2">
              Giới tính
            </label>
            <div className="flex gap-6">
              <label>
                <input
                  type="radio"
                  name="gender"
                  value={1}
                  checked={form.gender === 1}
                  onChange={handleChange}
                />{" "}
                Nam
              </label>
              <label>
                <input
                  type="radio"
                  name="gender"
                  value={0}
                  checked={form.gender === 0}
                  onChange={handleChange}
                />{" "}
                Nữ
              </label>
            </div>
          </div>

          <div className="flex justify-end">
            <button className="bg-blue-600 text-white px-6 py-2 rounded-lg">
              Lưu thay đổi
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
