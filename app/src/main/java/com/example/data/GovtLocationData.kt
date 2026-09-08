package com.example.data

data class GovtStateHierarchy(
    val stateName: String,
    val stateNameHi: String,
    val districts: Map<String, List<String>>
)

object GovtLocationRepository {
    val statesList: List<GovtStateHierarchy> = listOf(
        GovtStateHierarchy(
            stateName = "Delhi (NCT)",
            stateNameHi = "दिल्ली (राष्ट्रीय राजधानी क्षेत्र)",
            districts = mapOf(
                "South Delhi" to listOf("Hauz Khas Block", "Saket Block", "Mehrauli Block", "Kalkaji Block"),
                "New Delhi" to listOf("Chanakyapuri Block", "Connaught Place Block", "Delhi Cantt Block"),
                "North Delhi" to listOf("Civil Lines Block", "Kotwali Block", "Sadar Bazar Block"),
                "East Delhi" to listOf("Preet Vihar Block", "Mayur Vihar Block", "Gandhi Nagar Block"),
                "West Delhi" to listOf("Patel Nagar Block", "Rajouri Garden Block", "Punjabi Bagh Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Uttar Pradesh",
            stateNameHi = "उत्तर प्रदेश",
            districts = mapOf(
                "Lucknow" to listOf("Sarojini Nagar Block", "Bakshi Ka Talab Block", "Chinhat Block", "Malihabad Block", "Mohanlalganj Block"),
                "Noida (GB Nagar)" to listOf("Bisrakh Block", "Dadri Block", "Jewar Block", "Dankaur Block"),
                "Varanasi" to listOf("Kashi Vidyapeeth Block", "Arajiline Block", "Pindra Block", "Cholapur Block"),
                "Kanpur Nagar" to listOf("Kalyanpur Block", "Sarsaul Block", "Bidhnu Block", "Ghatampur Block"),
                "Prayagraj" to listOf("Bahadurpur Block", "Chaka Block", "Kaurihar Block", "Phulpur Block"),
                "Agra" to listOf("Bichpuri Block", "Khandauli Block", "Fatehabad Block", "Etmadpur Block"),
                "Gorakhpur" to listOf("Pipraich Block", "Bhathat Block", "Campierganj Block", "Sahjanwa Block"),
                "Ghaziabad" to listOf("Razapur Block", "Bhojpur Block", "Muradnagar Block", "Loni Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Bihar",
            stateNameHi = "बिहार",
            districts = mapOf(
                "Patna" to listOf("Patna Sadar Block", "Danapur Block", "Phulwari Sharif Block", "Fatwah Block", "Bihta Block"),
                "Gaya" to listOf("Bodh Gaya Block", "Gaya Town Block", "Sherghati Block", "Tekari Block"),
                "Muzaffarpur" to listOf("Mushahari Block", "Kanti Block", "Motipur Block", "Marwan Block"),
                "Bhagalpur" to listOf("Jagdishpur Block", "Nathnagar Block", "Sabour Block", "Sultanganj Block"),
                "Darbhanga" to listOf("Darbhanga Sadar Block", "Bahadurpur Block", "Keoti Block", "Jale Block"),
                "Purnia" to listOf("Purnia East Block", "Kasba Block", "Krityanand Nagar Block", "Banmankhi Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Rajasthan",
            stateNameHi = "राजस्थान",
            districts = mapOf(
                "Jaipur" to listOf("Sanganer Block", "Amber Block", "Jhotwara Block", "Chaksu Block", "Bassi Block"),
                "Jodhpur" to listOf("Mandore Block", "Luni Block", "Bhopalgarh Block", "Bilara Block"),
                "Kota" to listOf("Ladpura Block", "Sultanpur Block", "Itawa Block", "Sangod Block"),
                "Udaipur" to listOf("Girwa Block", "Badgaon Block", "Mavli Block", "Salumber Block"),
                "Bikaner" to listOf("Bikaner Block", "Nokha Block", "Kolayat Block", "Lunkaransar Block"),
                "Ajmer" to listOf("Ajmer Urban Block", "Srinagar Block", "Peesangan Block", "Kishangarh Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Madhya Pradesh",
            stateNameHi = "मध्य प्रदेश",
            districts = mapOf(
                "Bhopal" to listOf("Phanda Block", "Berasia Block", "Huzur Block"),
                "Indore" to listOf("Indore Urban Block", "Sanwer Block", "Mhow Block", "Depalpur Block"),
                "Gwalior" to listOf("Gwalior Urban Block", "Morar Block", "Ghatigaon Block", "Dabra Block"),
                "Jabalpur" to listOf("Jabalpur Block", "Panagar Block", "Patan Block", "Sihora Block"),
                "Ujjain" to listOf("Ujjain Urban Block", "Ghatiya Block", "Tarana Block", "Mahidpur Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Maharashtra",
            stateNameHi = "महाराष्ट्र",
            districts = mapOf(
                "Mumbai Suburban" to listOf("Andheri Block", "Borivali Block", "Kurla Block"),
                "Pune" to listOf("Haveli Block", "Mulshi Block", "Baramati Block", "Shirur Block"),
                "Nagpur" to listOf("Nagpur Urban Block", "Kamptee Block", "Hingna Block", "Katol Block"),
                "Thane" to listOf("Thane Block", "Kalyan Block", "Bhiwandi Block", "Ulhasnagar Block"),
                "Nashik" to listOf("Nashik Block", "Niphad Block", "Sinnar Block", "Dindori Block")
            )
        ),
        GovtStateHierarchy(
            stateName = "Haryana",
            stateNameHi = "हरियाणा",
            districts = mapOf(
                "Gurugram" to listOf("Gurugram Block", "Sohna Block", "Pataudi Block", "Farrukhnagar Block"),
                "Faridabad" to listOf("Faridabad Block", "Ballabgarh Block"),
                "Karnal" to listOf("Karnal Block", "Gharaunda Block", "Indri Block", "Assandh Block"),
                "Ambala" to listOf("Ambala-I Block", "Ambala-II Block", "Barara Block", "Saha Block")
            )
        )
    )

    val schoolManagementCategories = listOf(
        "Private Unaided (CBSE/ICSE/State Board)",
        "State Government / Zila Parishad",
        "Kendriya Vidyalaya (KVS)",
        "Jawahar Navodaya Vidyalaya (JNV)",
        "Government Aided / Trust Institution"
    )

    fun getDistrictsForState(stateName: String): List<String> {
        val found = statesList.firstOrNull { it.stateName.equals(stateName, ignoreCase = true) }
        return found?.districts?.keys?.toList() ?: listOf("Central District", "North District", "South District", "East District", "West District")
    }

    fun getBlocksForDistrict(stateName: String, districtName: String): List<String> {
        val found = statesList.firstOrNull { it.stateName.equals(stateName, ignoreCase = true) }
        return found?.districts?.get(districtName) ?: listOf("Central Block / Tehsil", "Sadar Block", "Sub-Divisional Block")
    }
}
