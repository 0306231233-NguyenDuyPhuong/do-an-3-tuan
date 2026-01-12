import { where, Op } from "sequelize";
import db from "../models/index.js";
import REPORTSTATUS from "../constants/ReportStatus.js"

const getReport = async (req, res) => {
  try {
    const { report_type, status, report_id, date, dateStart, dateEnd } = req.query;
    const page = Number(req.query.page) || 1;
    const limit = 10;
    const offset = (page - 1) * limit;
    let createdFilter = {};

    if (dateStart && dateEnd) {
      createdFilter = {
        [Op.between]: [
          `${dateStart} 00:00:00`,
          `${dateEnd} 23:59:59`
        ]
      };
    } else if (date) {
      createdFilter = {
        [Op.between]: [
          `${date} 00:00:00`,
          `${date} 23:59:59`
        ]
      };
    }


    const whereReport = {
      ...(report_type && {
        target_type: report_type
      }),
      ...(status !== undefined &&
        { status: Number(status) }
      ),
      ...(report_id && {
        id: Number(report_id)
      }),
      ...(Object.keys(createdFilter).length && {
        created_at: createdFilter
      })
    }
    const reportData = await db.Report.findAndCountAll({
      offset,
      limit,
      where: whereReport
    })
    return res.status(200).json({
      message: "Get report success",
      page,
      limit,
      data: reportData
    });
  } catch (error) {
    return res.status(500).json({
      error: error
    })
  }
};

const getReportById = async (req, res) => {
  return res.status(200).json({ message: "ok" });
};

const postReport = async (req, res) => {
  const { reporter_id, target_id, target_type, reason, description } = req.body;
  let targetCheck;
  if (target_type == "user") {
    targetCheck = await db.User.findOne({
      where: { id: target_id }
    })
  } else if (target_type == "post") {
    targetCheck = await db.Post.findOne({
      where: { id: target_id }
    })
  } else {
    targetCheck = await db.Comment.findOne({
      where: { id: target_id }
    })
  }
  if (!targetCheck) {
    return res.status(404).json({
      message: "target not found"
    });
  } else {
    const reportData = await db.Report.create({
      reporter_id,
      target_type,
      target_id,
      reason,
      description,
      status: REPORTSTATUS.PENDING
    });
    return res.status(201).json({
      message: "Insert report success",
      data: reportData
    });
  }
};

const putReport = async (req, res) => {
  const { id } = req.params;
  const reportData = await db.Report.findByPk(id);
  if (!reportData) {
    return res.status(404).json({
      message: "Report not found"
    });
  }
  await db.Report.update(req.body, { where: { id } })
  return res.status(200).json({
    message: "Report update success"
  })
};
const deleteReport = async (req, res) => {
  const { id } = req.params;
  const locationCheck = await db.Report.findByPk(id);
  if (!locationCheck) {
    return res.status(200).json({
      message: "Location not found",
    });
  } else {
    await db.Report.destroy({ where: { id } });
    return res.status(200).json({
      message: "Delete report success",
    });
  }
};

export default {
  getReport,
  getReportById,
  postReport,
  putReport,
  deleteReport,
};
