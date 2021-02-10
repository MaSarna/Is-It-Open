package com.example.isitopen

import java.io.Serializable


//Location's database adapter

class Locations (var name: String, var latitude: Double, var longitude: Double) : Serializable {

    constructor() : this("", 0.0, 0.0)
}
