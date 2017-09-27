(defproject cn.leancloud/hbase-toolkit "0.1.0"
  :description "Fixes/patches backport to HBase 1.2.0-cdh5.7.4"
  :url "http://github.com/leancloud/hbase-toolkit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["java_src"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cn.leancloud/clojure-hbase "1.2.0-cdh5.7.4-2"]]
  :repositories [["cloudera" "https://repository.cloudera.com/content/groups/public/"]]
  :profiles {:dev {:dependencies [[org.apache.hbase/hbase-testing-util "1.2.0-cdh5.7.4"]]}})
