package com.example.ums

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startLoginActivity()
//        startMainPageActivity("AAA",1,"SUPER_ADMIN")
//        startManageProfileActivity(1)
    }

    private fun startLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainPageActivity(userName : String, userID : Int, userRole : String){

        val intent = Intent(this, SuperAdminCollegeAdminMainPageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userName", userName)
        bundle.putInt("userID", userID)
        bundle.putString("userRole", userRole)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun startManageProfileActivity(userID : Int){
        val intent = Intent(this, ManageProfileActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("userID", userID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}
