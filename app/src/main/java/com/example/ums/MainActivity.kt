package com.example.ums

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        startLoginActivity()
        startMainPageActivity("AAA","1","SUPER_ADMIN")
    }

    private fun startLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainPageActivity(userName : String, userID : String, userRole : String){

        val intent = Intent(this, MainPageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userName", userName)
        bundle.putString("userID", userID)
        bundle.putString("userRole", userRole)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}
