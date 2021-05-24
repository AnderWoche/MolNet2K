package de.moldy.molnet2k.exchange

class MethodID(var stringID: String, var intID: Int) {

    constructor() : this("", 0)

    constructor(stringID: String) : this(stringID, 0)

    constructor(intID: Int) : this("", intID)

}