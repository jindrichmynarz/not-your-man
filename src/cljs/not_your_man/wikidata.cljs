(ns not-your-man.wikidata)

(def nspace "http://www.wikidata.org/prop/direct/")

(def properties
  {; Location
   ::P276 {:article? true
           :verb "is located at"}
   ; Subclass of
   ::P279 {:article? true
           :verb "is"}
   ; Use
   ::P366 {:verb "is used for"}
   ; Colour
   ::P462 {:verb "is"}
   ; Country of origin
   ::P495 {:verb "is from"}
   ; Has part
   ::P527 {:article? true
           :verb "has"}
   ; Different from 
   ::P1889 {:article? true
            :verb "is not"}})
