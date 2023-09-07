package org.piramalswasthya.cho.model

import java.io.Serializable

data class PrescriptionValues(
    var form: String = "",
    var medicine: String = "",
    var dosage: String = "",
    var frequency: String = "",
    var duration: String = "",
    var instruction: String = "",
    var unit: String = "",
    var route: String = ""
):Serializable