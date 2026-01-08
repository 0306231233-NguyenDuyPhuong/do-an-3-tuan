import { date } from "joi";
import db from "../models/index"

const getLocation = async(req, res) => {
    const locationData = await db.Location.findAll();
    return res.status(200).json({
        message: "Location",
        data: locationData
    });
}

const getLocationById = async(req, res) => {
    const {id} = req.params;
    const locationData = await db.Location.findByPk(id)
    if(!locationData){
        return res.status(200).json({
        message: "Location not found",
    });
    } else{
        return res.status(200).json({
        message: "Location",
        data: locationData
    });
    }
    
}

const postLocation = async(req, res) => {
    const locationData = await db.Location.create(req.body)
    return res.status(201).json({
        message: "Insert location success",
        data: locationData
    });
}

const putLocation = async(req, res) => {
    const {id} = req.params;
    const locationCheck = await db.Location.findByPk(id);
    if(!locationCheck){
        return res.status(404).json({
        message: "Location not found",
    });
    } else{
        await db.Location.update(req.body, {where: {id}});
        return res.status(200).json({
        message: "Update location success",
        });
    }
}

const deleteLocation = async(req, res) => {
    const {id} = req.params;
    const locationCheck = await db.Location.findByPk(id);
    if(!locationCheck){
        return res.status(200).json({
        message: "Location not found",
    });
    } else{
        await db.Location.destroy({where: {id}});
        return res.status(200).json({
        message: "Delete location success",
        });
    }
}

export default {
    getLocation:getLocation,
    getLocationById:getLocationById,
    postLocation:postLocation,
    putLocation:putLocation,
    deleteLocation:deleteLocation,
}