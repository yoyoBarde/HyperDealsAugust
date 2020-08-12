package com.example.kent.hyperdeals.FragmentsBusiness

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import com.example.kent.hyperdeals.FragmentActivities.FragmentCategory
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.R
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.fragment_data_control.*
import kotlinx.android.synthetic.main.fragment_fragment_dashboard_business.*
import kotlinx.android.synthetic.main.fragmentpromaplist.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.uiThread


class FragmentDashboardBusiness : Fragment() {
    val TAG = "fragmentBusiness"
    var database = FirebaseFirestore.getInstance()
    var userViews = ArrayList<UserModelParce>()
    var userLikes = ArrayList<UserModelParce>()
    var userNotified = ArrayList <UserModelParce>()
    var userDismissed = ArrayList <UserModelParce>()
    var executed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_dashboard_business, container, false)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

     businessman_demography_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            filterDemography()


            }
        }

        businessman_demography_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            filterDemography()

            }
        }

        businessman_demography_status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        filterDemography()

            }
        }



        var ageAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(activity!!,R.array.promo_demography_age,android.R.layout.simple_spinner_item)
        var genderAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(activity!!,R.array.promo_demography_gender,android.R.layout.simple_spinner_item)
        var statusAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(activity!!,R.array.promo_demography_status,android.R.layout.simple_spinner_item)

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        businessman_demography_age.adapter = ageAdapter
        businessman_demography_gender.adapter = genderAdapter
        businessman_demography_status.adapter = statusAdapter

        var mypromo = PromoListAdapter.promoProfile 
        getDemography(mypromo)

    chart_pie.onClick { showDialog() }
    }
    fun getDemography (mypromo:PromoModel){

Log.e(TAG,"Retrieve ${mypromo.promoname}    ${mypromo.promoID}")
doAsync {
    database.collection("PromoData").document("PromoViews").collection("Promos").document(mypromo.promoID).collection("Users").get().addOnCompleteListener { task ->
        if (task.isSuccessful) {

            for (DocumentSnapshot in task.result) {
                var UserView = DocumentSnapshot.toObject(UserModelParce::class.java)

                userViews.add(UserView)
            }
            Log.e(TAG,"${userViews.size} user view retrieve")

        } else {
            Log.e(TAG, "wala ma add")
        }

        database.collection("PromoData").document("PromoLikes").collection("Promos").document(mypromo.promoID).collection("Users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                for (DocumentSnapshot in task.result) {
                    var UserLikes = DocumentSnapshot.toObject(UserModelParce::class.java)

                    userLikes.add(UserLikes)

                }
                Log.e(TAG,"${userLikes.size} user like retrieve")

            } else
                Log.e(TAG, "wala ma add")

            database.collection("PromoData").document("PromoAvailed").collection("Promos").document(mypromo.promoID).collection("Users").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    for (DocumentSnapshot in task.result) {
                        var UserAvailed = DocumentSnapshot.toObject(UserModelParce::class.java)

                        userNotified.add(UserAvailed)

                    }
                    Log.e(TAG,"${userNotified.size} user notified retrieve")

                    //     Log.e(TAG, "LooptheListView task successful ${myPromoList[i].promoname} user availed -  ${myPromoList[i].userListAvailed.size} ")

                } else
                    Log.e(TAG, "wala ma add")

                database.collection("PromoData").document("PromoDismissed").collection("Promos").document(mypromo.promoID).collection("Users").get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (DocumentSnapshot in task.result) {
                            var PromoDismissed = DocumentSnapshot.toObject(UserModelParce::class.java)

                            userDismissed.add(PromoDismissed)

                        }
                        Log.e(TAG,"${userDismissed.size} user dismissed retrieve")
                        //    Log.e(TAG, "LooptheListView task successful ${myPromoList[i].promoname} user dismissed -  ${myPromoList[i].userListDismissed.size} ")
                    } else
                        Log.e(TAG, "wala ma add")

                    executed = true
                    Log.e(TAG,"userView - ${userViews.size}  userLikes - ${userLikes.size} userNotified - ${userNotified.size} - userDismissed - ${userDismissed.size}")
                    uiThread {
                        filterDemography()
                    }
                }


            }


        }


    }

}



        }

    fun filterDemography(){
        var myuserViews = ArrayList<UserModelParce>()
        var myuserLikes = ArrayList<UserModelParce>()
        var myuserNotified = ArrayList <UserModelParce>()
        var myuserDismissed = ArrayList <UserModelParce>()

        var myuserViews1 = ArrayList<UserModelParce>()
        var myuserLikes1= ArrayList<UserModelParce>()
        var myuserNotified1= ArrayList <UserModelParce>()
        var myuserDismissed1 = ArrayList <UserModelParce>()

        var myuserViews2 = ArrayList<UserModelParce>()
        var myuserLikes2= ArrayList<UserModelParce>()
        var myuserNotified2 = ArrayList <UserModelParce>()
        var myuserDismissed2 = ArrayList <UserModelParce>()

                if( businessman_demography_age.selectedItem.toString().equals("All")){
            for(i in 0 until userViews.size){

                myuserViews.add(userViews[i])
            }
            for(i in 0 until userLikes.size){

                myuserLikes.add(userLikes[i])
            }
            for(i in 0 until userNotified.size){

                myuserNotified.add(userNotified[i])
            }
            for(i in 0 until userDismissed.size){

                myuserDismissed.add(userDismissed[i])
            }

        }
        else if( businessman_demography_age.selectedItem.toString().equals("Adult")){
            for(i in 0 until userViews.size){

                if(userViews[i].Age.equals("Adult")) {
                    myuserViews.add(userViews[i])
                }
            }
            for(i in 0 until userLikes.size){

                if(userLikes[i].Age.equals("Adult")) {
                    myuserLikes.add(userLikes[i])
                }
            }
            for(i in 0 until userNotified.size){

                if(userNotified[i].Age.equals("Adult")){
                    myuserNotified.add(userNotified[i])
                }
            }
            for(i in 0 until userDismissed.size){

                if(userDismissed[i].Age.equals("Adult")) {
                    myuserDismissed.add(userDismissed[i])
                }
            }


        }
        else if( businessman_demography_age.selectedItem.toString().equals("Teenager")){
            for(i in 0 until userViews.size){

                if(userViews[i].Age.equals("Teenager")) {
                    myuserViews.add(userViews[i])
                }
            }
            for(i in 0 until userLikes.size){

                if(userLikes[i].Age.equals("Teenager")) {
                    myuserLikes.add(userLikes[i])
                }
            }
            for(i in 0 until userNotified.size){

                if(userNotified[i].Age.equals("Teenager")){
                    myuserNotified.add(userNotified[i])
                }
            }
            for(i in 0 until userDismissed.size){

                if(userDismissed[i].Age.equals("Teenager")) {
                    myuserDismissed.add(userDismissed[i])
                }
            }

        }
        else if( businessman_demography_age.selectedItem.toString().equals("Kids")){
            for(i in 0 until userViews.size){

                if(userViews[i].Age.equals("Kids")) {
                    myuserViews.add(userViews[i])
                }
            }
            for(i in 0 until userLikes.size){

                if(userLikes[i].Age.equals("Kids")) {
                    myuserLikes.add(userLikes[i])
                }
            }
            for(i in 0 until userNotified.size){

                if(userNotified[i].Age.equals("Kids")){
                    myuserNotified.add(userNotified[i])
                }
            }
            for(i in 0 until userDismissed.size){

                if(userDismissed[i].Age.equals("Kids")) {
                    myuserDismissed.add(userDismissed[i])
                }
            }

        }


        if( businessman_demography_gender.selectedItem.toString().equals("Male/Female")){
            for(i in 0 until myuserViews.size){

                myuserViews1.add(myuserViews[i])


            }
            for(i in 0 until myuserLikes.size){

                myuserLikes1.add(myuserLikes[i])


            }
            for(i in 0 until myuserNotified.size){

                myuserNotified1.add(myuserNotified[i])


            }
            for(i in 0 until myuserDismissed.size){

                myuserDismissed1.add(myuserDismissed[i])


            }


        }
        else if( businessman_demography_gender.selectedItem.toString().equals("Male")) {
            for (i in 0 until myuserViews.size) {

                if (myuserViews[i].Gender.equals("Male")) {
                    myuserViews1.add(myuserViews[i])
                }
            }
            for (i in 0 until myuserLikes.size) {

                if (myuserLikes[i].Gender.equals("Male")) {
                    myuserLikes1.add(myuserLikes[i])
                }
            }
            for (i in 0 until myuserNotified.size) {

                if (myuserNotified[i].Gender.equals("Male")) {
                    myuserNotified1.add(myuserNotified[i])
                }
            }
            for (i in 0 until myuserDismissed.size) {

                if (myuserDismissed[i].Gender.equals("Male")) {
                    myuserDismissed1.add(myuserDismissed[i])
                }

            }
        }
        else if( businessman_demography_gender.selectedItem.toString().equals("Female")) {
            for (i in 0 until myuserViews.size) {

                if (myuserViews[i].Gender.equals("Female")) {
                    myuserViews1.add(myuserViews[i])
                }
            }
            for (i in 0 until myuserLikes.size) {

                if (myuserLikes[i].Gender.equals("Female")) {
                    myuserLikes1.add(myuserLikes[i])
                }
            }
            for (i in 0 until myuserNotified.size) {

                if (myuserNotified[i].Gender.equals("Female")) {
                    myuserNotified1.add(myuserNotified[i])
                }
            }
            for (i in 0 until myuserDismissed.size) {

                if (myuserDismissed[i].Gender.equals("Female")) {
                    myuserDismissed1.add(myuserDismissed[i])
                }

            }
        }


        if( businessman_demography_status.selectedItem.toString().equals("Single/Married")){

            for(i in 0 until myuserViews1.size){

                myuserViews2.add(myuserViews1[i])


            }

            for(i in 0 until myuserLikes1.size){

                myuserLikes2.add(myuserLikes1[i])


            }
            for(i in 0 until myuserNotified1.size){

                myuserNotified2.add(myuserNotified1[i])


            }
            for(i in 0 until myuserDismissed1.size){

                myuserDismissed2.add(myuserDismissed1[i])


            }
        }
        else if( businessman_demography_status.selectedItem.toString().equals("Single")){

            for(i in 0 until myuserViews1.size){

                if(myuserViews1[i].Status.equals("Single")) {
                    myuserViews2.add(myuserViews1[i])
                }
            }
            for(i in 0 until myuserLikes1.size){

                if(myuserLikes1[i].Status.equals("Single")) {
                    myuserLikes2.add(myuserLikes1[i])
                }
            }
            for(i in 0 until myuserNotified1.size){

                if(myuserNotified1[i].Status.equals("Single")) {
                    myuserNotified2.add(myuserNotified1[i])
                }
            }
            for(i in 0 until myuserDismissed1.size){

                if(myuserDismissed1[i].Status.equals("Single")) {
                    myuserDismissed2.add(myuserDismissed1[i])
                }
            }

        }
        else if( businessman_demography_status.selectedItem.toString().equals("Married")){

            for(i in 0 until myuserViews1.size){

                if(myuserViews1[i].Status.equals("Married")) {
                    myuserViews2.add(myuserViews1[i])
                }
            }
            for(i in 0 until myuserLikes1.size){

                if(myuserLikes1[i].Status.equals("Married")) {
                    myuserLikes2.add(myuserLikes1[i])
                }
            }
            for(i in 0 until myuserNotified1.size){

                if(myuserNotified1[i].Status.equals("Married")) {
                    myuserNotified2.add(myuserNotified1[i])
                }
            }
            for(i in 0 until myuserDismissed1.size){

                if(myuserDismissed1[i].Status.equals("Married")) {
                    myuserDismissed2.add(myuserDismissed1[i])
                }
            }
        }



        businessman_views_count.text = myuserViews2.size.toString()
        businessan_likes_count.text  = myuserLikes2.size.toString()
        businessan_notified_count.text = myuserNotified2.size.toString()
        businessan_dismissed_count.text = myuserDismissed2.size.toString()



    }
    fun showDialog() {

        var demography = 0
        var btnViewBol = false
        var btnLikeBol = false
        var btnDismissBol = false
        var btnNotifiedBol = false
        var actiontxt = "Views"

        var btnGenderBol = false
        var btnAgeBol = false
        var btnStatusBol = false




        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_chart, null)

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)




        var pieChart = dialogView.findViewById(R.id.pieChart) as com.github.mikephil.charting.charts.PieChart
        var btnView = dialogView.findViewById(R.id.btnView) as Button
        var btnLike = dialogView.findViewById(R.id.btnLike) as Button
        var btnAviled = dialogView.findViewById(R.id.btnAvailed) as Button
        var btnDismiss = dialogView.findViewById(R.id.btnDismiss) as Button

        var btnGender = dialogView.findViewById(R.id.btnGender) as Button
        var btnAge = dialogView.findViewById(R.id.btnAge) as Button
        var btnStatus = dialogView.findViewById(R.id.btnStatus) as Button


        btnView.setTextColor(Color.parseColor("#FFFFFF"))
        btnView.setBackgroundResource(R.drawable.gradientbluefinal)
        btnGender.setBackgroundResource(R.drawable.gradientbluefinal)
        btnGender.setTextColor(Color.parseColor("#FFFFFF"))
        btnGenderBol = true
        btnViewBol = true
        showPieChart(pieChart,userViews,demography,actiontxt)


        btnView.onClick {
            actiontxt = "Views"


            btnView.setTextColor(Color.parseColor("#000000"))
            btnLike.setTextColor(Color.parseColor("#000000"))
            btnDismiss.setTextColor(Color.parseColor("#000000"))
            btnAviled.setTextColor(Color.parseColor("#000000"))
            btnView.setBackgroundResource(R.drawable.button_curve)
            btnLike.setBackgroundResource(R.drawable.button_curve)
            btnDismiss.setBackgroundResource(R.drawable.button_curve)
            btnAviled.setBackgroundResource(R.drawable.button_curve)



            if(!btnViewBol) {
                btnView.setTextColor(Color.parseColor("#FFFFFF"))
                btnView.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userViews,demography,actiontxt)



            }
            btnViewBol = false
            btnLikeBol = false
            btnDismissBol = false
            btnNotifiedBol = false

            btnViewBol = true


        }
        btnLike.onClick {
            actiontxt = "Likes"

            btnViewBol = false
            btnLikeBol = false
            btnDismissBol = false
            btnNotifiedBol = false

            btnView.setTextColor(Color.parseColor("#000000"))
            btnLike.setTextColor(Color.parseColor("#000000"))
            btnDismiss.setTextColor(Color.parseColor("#000000"))
            btnAviled.setTextColor(Color.parseColor("#000000"))
            btnView.setBackgroundResource(R.drawable.button_curve)
            btnLike.setBackgroundResource(R.drawable.button_curve)
            btnDismiss.setBackgroundResource(R.drawable.button_curve)
            btnAviled.setBackgroundResource(R.drawable.button_curve)



            if(!btnLikeBol) {
                btnLike.setTextColor(Color.parseColor("#FFFFFF"))
                btnLike.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userLikes,demography,actiontxt)



            }

            btnLikeBol = true

        }
        btnAviled.onClick {
            actiontxt = "Notified"

            btnViewBol = false
            btnLikeBol = false
            btnDismissBol = false
            btnNotifiedBol = false

            btnView.setTextColor(Color.parseColor("#000000"))
            btnLike.setTextColor(Color.parseColor("#000000"))
            btnDismiss.setTextColor(Color.parseColor("#000000"))
            btnAviled.setTextColor(Color.parseColor("#000000"))
            btnView.setBackgroundResource(R.drawable.button_curve)
            btnLike.setBackgroundResource(R.drawable.button_curve)
            btnDismiss.setBackgroundResource(R.drawable.button_curve)
            btnAviled.setBackgroundResource(R.drawable.button_curve)



            if(!btnNotifiedBol) {
                btnAviled.setTextColor(Color.parseColor("#FFFFFF"))
                btnAviled.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userNotified,demography,actiontxt)



            }

            btnNotifiedBol = true
        }
        btnDismiss.onClick {

            actiontxt = "Dismiss"

            btnViewBol = false
            btnLikeBol = false
            btnDismissBol = false
            btnNotifiedBol = false

            btnView.setTextColor(Color.parseColor("#000000"))
            btnLike.setTextColor(Color.parseColor("#000000"))
            btnDismiss.setTextColor(Color.parseColor("#000000"))
            btnAviled.setTextColor(Color.parseColor("#000000"))
            btnView.setBackgroundResource(R.drawable.button_curve)
            btnLike.setBackgroundResource(R.drawable.button_curve)
            btnDismiss.setBackgroundResource(R.drawable.button_curve)
            btnAviled.setBackgroundResource(R.drawable.button_curve)



            if(!btnDismissBol) {
                btnDismiss.setTextColor(Color.parseColor("#FFFFFF"))
                btnDismiss.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userDismissed,demography,actiontxt)



            }

            btnDismissBol = true


        }

        btnGender.onClick {

demography=0
            btnGender.setTextColor(Color.parseColor("#000000"))
            btnAge.setTextColor(Color.parseColor("#000000"))
            btnStatus.setTextColor(Color.parseColor("#000000"))
            btnGender.setBackgroundResource(R.drawable.button_curve)
            btnAge.setBackgroundResource(R.drawable.button_curve)
            btnStatus.setBackgroundResource(R.drawable.button_curve)



            if(!btnGenderBol) {
                btnGender.setTextColor(Color.parseColor("#FFFFFF"))
                btnGender.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userViews,demography,actiontxt)



            }
            btnGenderBol = false
            btnAgeBol = false
            btnStatusBol = false


            btnGenderBol = true


        }
        btnAge.onClick {
            demography=1
            btnGender.setTextColor(Color.parseColor("#000000"))
            btnAge.setTextColor(Color.parseColor("#000000"))
            btnStatus.setTextColor(Color.parseColor("#000000"))
            btnGender.setBackgroundResource(R.drawable.button_curve)
            btnAge.setBackgroundResource(R.drawable.button_curve)
            btnStatus.setBackgroundResource(R.drawable.button_curve)



            if(!btnAgeBol) {
                btnAge.setTextColor(Color.parseColor("#FFFFFF"))
                btnAge.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userViews,demography,actiontxt)



            }
            btnGenderBol = false
            btnAgeBol = false
            btnStatusBol = false


            btnAgeBol = true

        }
        btnStatus.onClick {

demography=2
            btnGender.setTextColor(Color.parseColor("#000000"))
            btnAge.setTextColor(Color.parseColor("#000000"))
            btnStatus.setTextColor(Color.parseColor("#000000"))
            btnGender.setBackgroundResource(R.drawable.button_curve)
            btnAge.setBackgroundResource(R.drawable.button_curve)
            btnStatus.setBackgroundResource(R.drawable.button_curve)



            if(!btnStatusBol) {
                btnStatus.setTextColor(Color.parseColor("#FFFFFF"))
                btnStatus.setBackgroundResource(R.drawable.gradientbluefinal)
                showPieChart(pieChart,userViews,demography,actiontxt)



            }
            btnGenderBol = false
            btnAgeBol = false
            btnStatusBol = false


            btnStatusBol = true

        }


        val b = dialogBuilder.create()
        b.show()
        b.setCancelable(true)



    }

    fun showPieChart(pieChart:com.github.mikephil.charting.charts.PieChart ,userAction:ArrayList<UserModelParce>,demography:Int,actiongtxt:String){



        var demographytxt ="Gender"
        var pieEntries= ArrayList<PieEntry>()

Log.e(TAG, "demography Int ${demography} action $actiongtxt ${userAction.size}")
        if(demography==0){
            demographytxt = "Gender"
            var maleCount = 0
            var femaleCount = 0

            for(i in 0 until userAction.size){

                if(userAction[i].Gender == "Male"){
                    maleCount++

                }
               else if(userAction[i].Gender=="Female"){
                    femaleCount++


                }


            }

            var percentMale = (maleCount.toFloat()*100.toFloat())/userAction.size.toFloat()
            var percentFemale = (femaleCount.toFloat()*100.toFloat())/userAction.size.toFloat()



            pieEntries.add(PieEntry(percentMale.toFloat(),"Male"))


            pieEntries.add(PieEntry(percentFemale.toFloat(),"Female"))

            Log.e(TAG,"maleCount ${maleCount} femaleCount ${femaleCount}")

            Log.e(TAG,"percentMale ${percentMale} percentFemale${percentFemale}")
        }
        else if (demography==1){
            demographytxt = "Age"

            var kidsCount = 0
            var teenagerCount = 0
            var adultCount = 0


            for(i in 0 until userAction.size){

                if(userAction[i].Age=="Kids"){
                    kidsCount++

                }
                else if(userAction[i].Age=="Teenager"){
                    teenagerCount++


                }
                else if(userAction[i].Age=="Adult"){
                    adultCount++


                }


            }



            var percentKids = (kidsCount.toFloat()*100.toFloat())/userAction.size.toFloat()
            var percentTeens = (teenagerCount.toFloat()*100.toFloat())/userAction.size.toFloat()

            var percentAdult = (adultCount.toFloat()*100.toFloat())/userAction.size.toFloat()


            pieEntries.add(PieEntry(percentKids.toFloat(),"Kids"))
            pieEntries.add(PieEntry(percentTeens.toFloat(),"Teenager"))
            pieEntries.add(PieEntry(percentAdult.toFloat(),"Adult"))


            Log.e(TAG,"percentKids $percentKids percentTeens $percentTeens percentAdult $percentAdult")

        }
        else if(demography==2){
            demographytxt = "Status"
            var singleCount = 0
            var marriedCount = 0

            for(i in 0 until userAction.size){

                if(userAction[i].Status=="Single"){
                    singleCount++

                }
                else if(userAction[i].Status=="Married"){
                    marriedCount++


                }


            }


            var percentSingle = (singleCount.toFloat()*100.toFloat())/userAction.size.toFloat()
            var percentMarried = (marriedCount.toFloat()*100.toFloat())/userAction.size.toFloat()

            pieEntries.add(PieEntry(percentSingle.toFloat(),"Single"))


            pieEntries.add(PieEntry(percentMarried.toFloat(),"Married"))

            Log.e(TAG,"percentSingle $percentSingle percentMarried $percentMarried")


        }



        pieChart.animateXY(2000,2000)
        var mPieDataset = PieDataSet(pieEntries,"Total $actiongtxt is ${userAction.size}")
        mPieDataset.setColors(intArrayOf(R.color.red, R.color.green, R.color.yellow, R.color.blue), activity)
        //  mBarDataSet.setColors(ColorTemplate.COLORFUL_COLORS)

        var pieData = PieData(mPieDataset)

        var description = Description()
        description.text = "Promo $actiongtxt percentage by $demographytxt"

        pieChart.data = pieData

        Log.e(TAG,"Pie entries number ${pieEntries.size}")

        pieChart.description = description
        pieChart.invalidate()


    }


}

