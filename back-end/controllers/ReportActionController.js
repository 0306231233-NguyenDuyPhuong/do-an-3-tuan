import { where } from "sequelize";
import db from "../models/index.js";

const getReportAction = async (req, res) => {
  try {
    const reprotActionData = await db.ReportAction.findAll({
    /*include: [
      { model: db.User },
      {
        model: db.Report,
        include: [
          {
            model: db.User
          }
        ]
      }
    ],*/
  });
  return res.status(200).json({ message: "Get report action", data: reprotActionData });
  } catch (error) {
    return res.status(400).json({
      error: error
    })
  }
  
};

const getReportActionById = async (req, res) => {
  const { id } = req.params;
  const reportActionData = await db.ReportAction.findByPk(id, {
    include: [
      { model: db.User },
      { model: db.Report }
    ],
  });
  if (reportActionData) {
    return res.status(200).json({ message: "Get report action", data: reportActionData });
  } else {
    return res.status(404).json({ message: "Report action not found" });
  }
};

const postReportAction = async (req, res) => {
  const { report_id, admin_id } = req.body;
  const [reportCheck, userCheck] = await Promise.all([
    db.Report.findOne({ where: { id: report_id } }),
    db.User.findOne({
      where: {
        id: admin_id,
        role: "admin"
      }
    })
  ])

  if (!reportCheck) {
    return res.status(404).json({
      message: "Report not found"
    })
  }
  if (!userCheck) {
    return res.status(404).json({
      message: "User not found or user not admin"
    })
  }
  const reportActionData = await db.ReportAction.create(req.body);
  return res.status(201).json({ message: "Insert report action success", data: reportActionData });
};

const putReportAction = async (req, res) => {
  const { admin_id } = req.body;
  const { id } = req.params;
  const userCheck = await db.User.findOne({
    where: {
      id: admin_id,
      role: "admin"
    }
  })

  if (!userCheck) {
    return res.status(404).json({
      message: "User not found or user not admin"
    })
  }
  await db.ReportAction.update(req.body, { where: { id } });
  return res.status(201).json({ message: "Update report action success" });
};

export default {
  getReportAction,
  getReportActionById,
  postReportAction,
  putReportAction,
};
