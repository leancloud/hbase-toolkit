(ns hbase-toolkit.filter-test
  (:require [clojure-hbase.core :as hb]
            [clojure-hbase.admin :as admin]
            [clojure.test :refer :all])
  (:import cn.leancloud.hbase.filter.InclusiveStopFilter
           org.apache.hadoop.hbase.HBaseTestingUtility
           org.apache.hadoop.hbase.util.Bytes))

(def ^:dynamic *test-util* (atom nil))

(defn- setup-cluster [^HBaseTestingUtility tu]
  (.startMiniCluster tu 1)
  (hb/set-config (.getConfiguration tu))
  (admin/set-admin-config (.getConfiguration tu))
  tu)

(defn once-start []
  (swap! *test-util*
         #(or % (setup-cluster (HBaseTestingUtility.)))))

(defn once-stop []
  (swap! *test-util* #(.shutdownMiniCluster %)))

(defn create-table []
  (admin/create-table (admin/table-descriptor :t1
                                              :family (admin/column-descriptor :f1))))

(defn once-fixture [f]
  (once-start)
  (create-table)
  (try
    (f)
    (finally
      (once-stop))))

(use-fixtures :once once-fixture)

(deftest test-inclusive-stop-filter []
  (hb/with-table [t (hb/table :t1)]
    (dotimes [i 10]
      (hb/put t i :values [:f1 [:foo i]]))
    (testing "reversed = false"
      (hb/with-scanner [r (hb/scan t 
                                   :reversed false
                                   :start-row 3
                                   :filter (InclusiveStopFilter. (hb/to-bytes 7))
                                   :max-versions 1)]
        (is (= (range 3 8)
               (map (fn [r]
                      (Bytes/toLong (.getRow r)))
                    (-> r .iterator iterator-seq))))))
    (testing "reversed = true"
      (hb/with-scanner [r (hb/scan t 
                                   :reversed true
                                   :start-row 7
                                   :filter (InclusiveStopFilter. (hb/to-bytes 3))
                                   :max-versions 1)]
        (is (= (range 7 2 -1)
               (map (fn [r]
                      (Bytes/toLong (.getRow r)))
                    (-> r .iterator iterator-seq))))))))
