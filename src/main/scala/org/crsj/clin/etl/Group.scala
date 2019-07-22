package org.crsj.clin.etl

import org.apache.spark.sql.functions.{explode, expr}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.crsj.clin.etl.DataFrameUtils.joinAggregateList

object Group {
  def load(base: String)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._

    val group = DataFrameUtils.load(s"$base/group.ndjson", $"id" as "group_id", explode($"member") as "patient")
    val study = DataFrameUtils.load(s"$base/study.ndjson", $"id" as "study_id", $"title", explode($"enrollment") as "enrollment")


    val groupWithStudy = study.select($"study_id", $"title", $"enrollment")
      .join(group.select($"group_id", $"patient"), $"enrollment.id" === $"group_id")
      .select($"study_id", $"title", $"patient", $"enrollment", $"group_id")


//    val studiesWithGroup = joinAggregateList(study, group,
//      expr("array_contains(enrollment.id, group_id)"), "group")


    groupWithStudy
//    studiesWithGroup
  }
}
