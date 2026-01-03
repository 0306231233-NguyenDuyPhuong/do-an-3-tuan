const postDetail = () =>{
    return( <>
        <div className="flex flex-col">
            <div>
                <span className="font-bold text-2xl">back</span>
            </div>
            <div className="flex gap-5">
                <div className="flex-2 h-200 w- min-[200] bg-gray-100 border border-gray-300 rounded-2xl shadow-md">
                    <div className="flex flex-col p-10">
                        <div className="flex gap-5 items-center">
                        <div className="size-20 border rounded-full">
                            <div className="flex justify-center items-center p-2">
                                avatar
                            </div>
                        </div>
                        <div>name user</div>
                        </div>

                        <div>item5</div>
                        <div className="flex justify-between">
                            <div>title</div>
                            <div>action</div>
                        </div>
                        <div className="flex-1 h-min[200] border">image list</div>
                        <div className="flex gap-5">
                            <div>like</div>
                            <div>item</div>
                        </div>
                    </div>
                </div>
                <div className="flex-1 h-200 w- min-[200] bg-gray-100 border border-gray-300 rounded-2xl shadow-md">item1</div>
            </div>
        </div>
    </>)
}

export default postDetail;