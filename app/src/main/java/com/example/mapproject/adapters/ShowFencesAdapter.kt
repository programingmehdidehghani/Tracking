package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.databinding.LayoutItemsFenceBinding
import com.example.mapproject.util.SphericalMercator
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon


interface OnItemClickCallback {
    fun onItemDeleteClick(id: String, position: Int)

    fun onItemEditCLick(id: String,position: Int,name: String,description: String,color: String)
}
class FencesAdapter (private val context: Context,private val onItemClickCallback: OnItemClickCallback) : RecyclerView.Adapter<FencesAdapter.FencesViewHolder>() {

    private val geoPointArrayList = ArrayList<GeoPoint>()
    private lateinit var geoPoint: GeoPoint
    private lateinit var latLng: LatLng
    private lateinit var mapView: MapView
    private val resultList: ArrayList<com.example.mapproject.model.showFences.Result> = arrayListOf()
    private lateinit var pointFinal : PointF


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FencesViewHolder {
        val binding = LayoutItemsFenceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FencesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: FencesViewHolder, position: Int) {
        holder.bind(resultList[position],onItemClickCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<com.example.mapproject.model.showFences.Result>) {
        this.resultList.clear()
        this.resultList.addAll(list)
        notifyDataSetChanged()
    }


    inner class FencesViewHolder(private val binding: LayoutItemsFenceBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(result: com.example.mapproject.model.showFences.Result,onItemClickCallback: OnItemClickCallback){
            binding.txtShowNameItemsFence.text = result.name
            val pointFs = ArrayList<PointF>()
            val polygon = Polygon()
            mapView = MapView(context)
            if (result.type == "circle"){
                val colorString = result.color
                val color = Color.parseColor("#$colorString")
                binding.circleViewInItemsFence.setColor(color)
                binding.circleViewInItemsFence.visibility = View.VISIBLE
                binding.polygonViewInItemsFence.visibility = View.GONE
            }
            if (result.type == "polygon"){
                val colorString = result.color
                val color = Color.parseColor("#$colorString")
                binding.polygonViewInItemsFence.setColor(color)
                binding.polygonViewInItemsFence.visibility = View.VISIBLE
                binding.circleViewInItemsFence.visibility = View.GONE
            }
        /*    for(i in result.shape){
                geoPoint = GeoPoint(i.latitude, i.longitude)
                pointFinal = PointF(SphericalMercator.lon2x(geoPoint.longitude).toFloat(),
                    SphericalMercator.lat2y(geoPoint.latitude).toFloat())
                //geoPointArrayList.add(geoPoint)
               *//* val projection = mapView.projection
                val point = projection.toPixels(geoPoint, null)
                val pointF = PointF(point.x.toFloat(), point.y.toFloat())*//*
                pointFs.add(pointFinal)
            }*/
            //val bitmap = drawGeofence(400,400,pointFs, specifyCornersPolyGon(pointFs) as ArrayList<PointF>)
            //polygon.points = geoPointArrayList
           // binding.polygonInItemsFence.setBitmap(bitmap)

            binding.ivDeletedFencesInItemsFences.setOnClickListener {
                onItemClickCallback.onItemDeleteClick(
                    result.id.toString(),
                    adapterPosition
                )
            }

            binding.ivEditInItemsFence.setOnClickListener {
                onItemClickCallback.onItemEditCLick(
                    result.id.toString(),
                    adapterPosition,
                    result.name,
                    result.description.toString(),
                    result.color
                )
            }

        }
    }

    fun specifyCornersPolyGon(pintF: ArrayList<PointF>) : List<PointF>{
        val pointFsList = mutableListOf<PointF>()
        var max : PointF= PointF(0f, 0f)
        var min : PointF= PointF(Float.MAX_VALUE, Float.MAX_VALUE)
        for (i in pintF){
            if (i.x > max.x){
                max.x = i.x
            }
            if (i.y > max.y){
                max.y = i.y
            }
            if (i.x < min.x){
                min.x = i.x
            }
            if (i.y < min.y){
                min.y = i.y
            }
        }
        pointFsList.add(min)
        pointFsList.add(max)
        return pointFsList
    }


    fun drawGeofence(width: Int,height: Int,pintF: ArrayList<PointF>,pintBBox: ArrayList<PointF>): Bitmap {
        val BBoxwidth = pintBBox[1].x - pintBBox[0].x
        val BBoxHeight = pintBBox[1].y - pintBBox[0].y
        val divideBBoxWidth = BBoxwidth / 2
        val divideBBoxHeight = BBoxHeight / 2
        val offsetX = pintBBox[0].x + divideBBoxWidth
        val offsetY = pintBBox[0].y + divideBBoxHeight
        //val path = Path()
//        val polygonPaint = Paint().apply {
//            color = Color.RED
//        }
        var finalPoint : ArrayList<PointF> = arrayListOf()
        for (i in pintF){
            finalPoint.add(
                PointF(
                    (i.x - offsetX) * 0.03F,
                    (i.y - offsetY) * 0.03F
                )
            )
        }
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
       // bitmap = BitmapFactory.decodeResource(context.resources, context.resources.getColor(R.color.RedLow))

        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.isAntiAlias = true

        val canvas = Canvas(bitmap)
        initCanvas(canvas)
        canvas.drawCircle(100f, 100f, 50f, paint)

        val path = Path()
        val firstPoint = finalPoint[0]
        path.moveTo(firstPoint.x, firstPoint.y)
        for (i in 1 until finalPoint.size) {
            val point = finalPoint[i]
            path.lineTo(point.x, point.y)
        }
        path.close()
        canvas?.drawPath(path, paint)
        return bitmap
    }

    fun drawCircle(color: Int){
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.isAntiAlias = true

        var bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        initCanvas(canvas)
        canvas.drawCircle(100f, 100f, 20f, paint)
    }

    fun initCanvas(canvas: Canvas){
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f

        // Translate the canvas to the center point
        canvas.translate(centerX, centerY)

        // Scale the canvas so that the origin is at the center point
        canvas.scale(1f, 1f)
        canvas.save()
/*        // Draw your graphics elements on the canvas here
        // ...

        // Restore the original transformation matrix
        canvas.restore()*/
    }





}