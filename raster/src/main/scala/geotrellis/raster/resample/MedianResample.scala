package geotrellis.raster.resample

import geotrellis.raster._
import geotrellis.raster.op.stats._
import geotrellis.raster.histogram._
import geotrellis.vector.Extent

import scala.collection.mutable
import collection._

import spire.syntax.cfor._

/**
  * Takes the ordinally-middlemost value in a region and resamples to that.
  *
  * As with other aggregate resampling methods, this is most useful when
  * decreasing resolution or downsampling.
  *
  */
class MedianResample(tile: Tile, extent: Extent, targetCS: CellSize)
    extends AggregateResample(tile, extent, targetCS) {

  private def calculateIntMedian(indices: Seq[(Int, Int)]): Int = {
    val contributions = indices.map { case (x, y) => tile.get(x, y) }.sorted
    val mid: Int = contributions.size / 2

    if (contributions.isEmpty) NODATA
    else if (contributions.size % 2 == 0) (contributions(mid - 1) + contributions(mid)) / 2
    else contributions(mid)
  }

  private def calculateDoubleMedian(indices: Seq[(Int, Int)]): Double = {
    val contributions = indices.map { case (x, y) => tile.getDouble(x, y) }.sorted
    val mid: Int = contributions.size / 2

    if (contributions.isEmpty) Double.NaN
    else if (contributions.size % 2 == 0) (contributions(mid - 1) + contributions(mid)) / 2
    else contributions(mid)
  }

  override def resampleValid(x: Double, y: Double): Int =
    calculateIntMedian(contributions(x, y))

  override def resampleDoubleValid(x: Double, y: Double): Double =
    calculateDoubleMedian(contributions(x, y))

}
