package com.example.closetfrontend

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.closetfrontend.RetrofitInterface.Companion.create
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookBookDetailViewActivity : AppCompatActivity() {
    private lateinit var heartIcon: ImageView
    private lateinit var exitIcon: ImageView
    private lateinit var commentText: TextView
    private lateinit var firstHashtag: TextView
    private lateinit var secondHashtag: TextView
    private lateinit var thirdHashtag: TextView
    private lateinit var firstLink: TextView
    private lateinit var secondLink: TextView
    private lateinit var thirdLink: TextView
    private lateinit var fourthLink: TextView
    private lateinit var fifthLink: TextView
    private lateinit var lastLink: TextView
    private lateinit var lookbookTop: ImageView
    private lateinit var lookbookBottom: ImageView
    private lateinit var lookbookOuter: ImageView
    private lateinit var lookbookOnepiece: ImageView
    private lateinit var lookbookShoes: ImageView
    private lateinit var lookbookBag: ImageView
    private lateinit var wholeBackground: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        //window.requestFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_book_detail_view)

        val codiId = intent.getStringExtra("codiId")
        //맞는지 확인
        heartIcon = findViewById(R.id.idHeartIcon)
        exitIcon = findViewById(R.id.idExitIcon)

        exitIcon.setOnClickListener {
            finish()
        }

        wholeBackground = findViewById(R.id.idDetailImage)

        commentText = findViewById(R.id.idCommentText)
        firstHashtag = findViewById(R.id.idFirstHashtag)
        secondHashtag = findViewById(R.id.idSecondHashtag)
        thirdHashtag = findViewById(R.id.idThirdHashtag)

        firstLink = findViewById(R.id.linkLookbookTop)
        secondLink = findViewById(R.id.linkLookbookBottom)
        thirdLink = findViewById(R.id.linkLookbookOuter)
        fourthLink = findViewById(R.id.linkLookbookOnepiece)
        fifthLink = findViewById(R.id.linkLookbookShoes)
        lastLink = findViewById(R.id.linkLookbookBag)

        lookbookTop = findViewById(R.id.lookbookTop)
        lookbookBottom = findViewById(R.id.lookbookBottom)
        lookbookOuter = findViewById(R.id.lookbookOuter)
        lookbookOnepiece = findViewById(R.id.lookbookOnepiece)
        lookbookShoes = findViewById(R.id.lookbookShoes)
        lookbookBag = findViewById(R.id.lookbookBag)

        getSelectedCodi(codiId)
    }

    private fun getSelectedCodi(codiId: String?) {
        val sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val retrofitInterface = create()
        retrofitInterface.getSelectedCodi(userId!!, codiId!!)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val codiData = response.body()
                        updateUI(codiData)
                    } else {
                        Toast.makeText(
                            this@LookBookDetailViewActivity,
                            "Failed to get codi information",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Toast.makeText(
                        this@LookBookDetailViewActivity,
                        "Network error. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateUI(codiData: JsonObject?) {
        if (codiData != null) {

            updateHashtags(codiData.getAsJsonArray("styles"))

            val comment: String = codiData.get("comment").asString
            commentText.setText(comment)

            val likeType: String = codiData.get("like").asString
            updateLikeIcon(likeType)

            updateClothesImages(codiData.getAsJsonArray("clothesImages"))

            val links: JsonArray = codiData.getAsJsonArray("links")
            updateClothesLinks(links)
        }
    }


    private fun updateClothesLinks(links: JsonArray) {
        if (links.size() >= 6) {
            // Check for JsonNull before accessing elements
            val topImageLink = links[0].takeIf { !it.isJsonNull }?.asString
            val bottomImageLink = links[1].takeIf { !it.isJsonNull }?.asString
            val outerImageLink = links[2].takeIf { !it.isJsonNull }?.asString
            val onepieceImageLink = links[3].takeIf { !it.isJsonNull }?.asString
            val shoesImageLink = links[4].takeIf { !it.isJsonNull }?.asString
            val bagImageLink = links[5].takeIf { !it.isJsonNull }?.asString

            wholeBackground.setOnClickListener {
                updateLinkTextView(topImageLink, firstLink)
                updateLinkTextView(bottomImageLink, secondLink)
                updateLinkTextView(outerImageLink, thirdLink)
                updateLinkTextView(onepieceImageLink, fourthLink)
                updateLinkTextView(shoesImageLink, fifthLink)
                updateLinkTextView(bagImageLink, lastLink)
            }
        }
    }

    private fun updateLinkTextView(link: String?, textView: TextView) {
        if (link != null) {
            textView.visibility = View.VISIBLE
            textView.text = link
            textView.setOnClickListener {
                val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("$link"))
                startActivity(intent)
            }
        } else {
            textView.visibility = View.INVISIBLE
        }
    }

    private fun updateHashtags(stylesArray: JsonArray) {
        if (stylesArray.size() >= 3) {
            firstHashtag.text = "# " + stylesArray[0].asString
            secondHashtag.text = "# " + stylesArray[1].asString
            thirdHashtag.text = "# " + stylesArray[2].asString
        } else {
            if (stylesArray.size() >= 1) {
                firstHashtag.text = "# " + stylesArray[0].asString
                secondHashtag.visibility = View.INVISIBLE
                thirdHashtag.visibility = View.INVISIBLE
            }
            if (stylesArray.size() >= 2) {
                secondHashtag.text = "# " + stylesArray[1].asString
                secondHashtag.visibility = View.VISIBLE
                thirdHashtag.visibility = View.INVISIBLE
            }
            if (stylesArray.size() >= 3) {
                thirdHashtag.text = "# " + stylesArray[2].asString
                thirdHashtag.visibility = View.VISIBLE
            }
        }
    }

    private fun updateLikeIcon(likeType: String) {
        if ("like" == likeType) {
            heartIcon.setImageResource(R.drawable.heart_filled)
        }
    }

    private fun updateClothesImages(clothesImagesArray: JsonArray) {
        if (clothesImagesArray.size() >= 6) {
            // Check for JsonNull before accessing elements
            val topImageUrl = clothesImagesArray[0].takeIf { !it.isJsonNull }?.asString
            val bottomImageUrl = clothesImagesArray[1].takeIf { !it.isJsonNull }?.asString
            val outerImageUrl = clothesImagesArray[2].takeIf { !it.isJsonNull }?.asString
            val onepieceImageUrl = clothesImagesArray[3].takeIf { !it.isJsonNull }?.asString
            val shoesImageUrl = clothesImagesArray[4].takeIf { !it.isJsonNull }?.asString
            val bagImageUrl = clothesImagesArray[5].takeIf { !it.isJsonNull }?.asString

            Glide.with(this).load("http://172.10.7.44:80/images/$topImageUrl").into(lookbookTop)
            Glide.with(this).load("http://172.10.7.44:80/images/$bottomImageUrl")
                .into(lookbookBottom)
            Glide.with(this).load("http://172.10.7.44:80/images/$outerImageUrl").into(lookbookOuter)
            Glide.with(this).load("http://172.10.7.44:80/images/$onepieceImageUrl")
                .into(lookbookOnepiece)
            Glide.with(this).load("http://172.10.7.44:80/images/$shoesImageUrl").into(lookbookShoes)
            Glide.with(this).load("http://172.10.7.44:80/images/$bagImageUrl").into(lookbookBag)
        }
//        }
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[0].asString}").into(lookbookTop)
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[1].asString}").into(lookbookBottom)
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[2].asString}").into(lookbookOuter)
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[3].asString}").into(lookbookOnepiece)
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[4].asString}").into(lookbookShoes)
//        Glide.with(this).load("http://172.10.7.44:80/images/${clothesImagesArray[5].asString}").into(lookbookBag)
//
//        Picasso.get().load("http://172.10.7.44:80/images/${clothesImagesArray[1]}").into(lookbookBottom)
//        Picasso.get().load("http://172.10.7.44:80/images/${clothesImagesArray[2]}").into(lookbookOuter)
//        Picasso.get().load("http://172.10.7.44:80/images/${clothesImagesArray[3]}").into(lookbookOnepiece)
//        Picasso.get().load("http://172.10.7.44:80/images/${clothesImagesArray[4]}").into(lookbookShoes)
//        Picasso.get().load("http://172.10.7.44:80/images/${clothesImagesArray[5]}").into(lookbookBag)
        //if (clothesImagesArray.size() >= 6) {

//            lookbookTop.setImageBitmap(displayProcessedImage(clothesImagesArray[0].asString))
//            lookbookBottom.setImageBitmap(displayProcessedImage(clothesImagesArray[1].asString))
//            lookbookOuter.setImageBitmap(displayProcessedImage(clothesImagesArray[2].asString))
//            lookbookOnepiece.setImageBitmap(displayProcessedImage(clothesImagesArray[3].asString))
//            lookbookShoes.setImageBitmap(displayProcessedImage(clothesImagesArray[4].asString))
//            lookbookBag.setImageBitmap(displayProcessedImage(clothesImagesArray[5].asString))
        //}
    }

}