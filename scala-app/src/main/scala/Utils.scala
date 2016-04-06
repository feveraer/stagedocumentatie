import org.apache.spark.sql.DataFrame

import scala.reflect.ClassTag

/**
  * Created by frederic on 6/04/16.
  */
object Utils {

  def getSeqFromDF[T:ClassTag](df: DataFrame, column: String): Seq[T] = {
    df.select(column).rdd.map(x => x(0).asInstanceOf[T]).collect().toSeq
  }
}
