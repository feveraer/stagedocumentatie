import com.datastax.driver.core.Row

/**
  * Created by Lorenz on 31.03.16.
  */
object CassandraIO {
  def main(args: Array[String]) {
    println("Hello Cassandra")

    val cassandra = new CassandraConnection()
    cassandra.connect()

    println("Create table persons")
    println()
    cassandra.executeQuery("CREATE TABLE testdata.persons("
      + "id int PRIMARY KEY, "
      + "age int, "
      + "name text)")


    println("Write data of 3 persons to Cassandra")
    println()
    cassandra.executeQuery("INSERT INTO testdata.persons (id, name, age) VALUES (1, 'Lorenz', 23)")
    cassandra.executeQuery("INSERT INTO testdata.persons (id, name, age) VALUES (2, 'Frederic', 24)")
    cassandra.executeQuery("INSERT INTO testdata.persons (id, name, age) VALUES (3, 'Thomas', 30)")

    val results = cassandra.executeQuery("SELECT * FROM persons")
    val resultIterator = results.iterator()

    println("Read")
    println()
    while(resultIterator.hasNext()){
      val row = resultIterator.next()
      printf("%s %d\n", row.getString("name"), row.getInt("age"))
    }

    println()
    println("You have 20 sec to check table on cassandra cluster (^_^)")
    Thread.sleep(20000)

    println("Remove table")
    println()
    cassandra.executeQuery("DROP TABLE testdata.persons")

    cassandra.close()
  }
}
