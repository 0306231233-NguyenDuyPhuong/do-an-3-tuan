import { create } from "zustand";
import { putUser } from "../services/UserService";

export const useUserStore = create((set) => ({
  user: JSON.parse(localStorage.getItem("user")),

  setUser: (user) => {
    localStorage.setItem("user", JSON.stringify(user));
    set({ user });
  },

  updateProfile: async (data) => {
    await putUser(data);

    const oldUser = JSON.parse(localStorage.getItem("user"));
    const newUser = { ...oldUser, ...data };

    localStorage.setItem("user", JSON.stringify(newUser));
    set({ user: newUser });
  },
}));
