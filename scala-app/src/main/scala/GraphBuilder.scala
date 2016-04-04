import breeze.linalg._
import breeze.plot._

/**
  * Created by frederic on 31/03/16.
  */
class GraphBuilder {

  def test() {
    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0,1.0)
    p += plot(x, x :^ 2.0)
    p += plot(x, x :^ 3.0, '.')
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    f.saveas("lines.png")
  }
}
