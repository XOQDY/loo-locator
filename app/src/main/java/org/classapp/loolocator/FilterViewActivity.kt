package org.classapp.loolocator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import android.widget.TextView
import android.widget.Switch

class FilterViewActivity : AppCompatActivity() {
    private lateinit var filterRangeSeekBar: SeekBar
    private lateinit var filterRangeValueTxt: TextView
    private lateinit var maleSwitch: Switch // Get a reference to the Switch
    private lateinit var femaleSwitch: Switch
    private lateinit var babySwitch: Switch
    private lateinit var prayerSwitch: Switch
    private lateinit var disabledSwitch: Switch
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (application as App).viewModel
        setContentView(R.layout.activity_filter_view)

        filterRangeSeekBar = findViewById(R.id.filterRangeSeekBar)
        filterRangeValueTxt = findViewById(R.id.filterRangeValueTxt)
        maleSwitch = findViewById(R.id.maleSwitch) // Initialize the Switch
        femaleSwitch = findViewById(R.id.femaleSwitch)
        babySwitch = findViewById(R.id.babySwitch)
        prayerSwitch = findViewById(R.id.prayerSwitch)
        disabledSwitch = findViewById(R.id.disabledSwitch)

        // Set the default state of the switch buttons to match the values in SharedViewModel
        maleSwitch.isChecked = viewModel.haveMale.value
        femaleSwitch.isChecked = viewModel.haveFemale.value
        babySwitch.isChecked = viewModel.haveBaby.value
        prayerSwitch.isChecked = viewModel.havePrayer.value
        disabledSwitch.isChecked = viewModel.haveDisabled.value

        filterRangeSeekBar.max = 5
        filterRangeSeekBar.progress = viewModel.maxRangeValue.intValue
        filterRangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.maxRangeValue.intValue = progress
                filterRangeValueTxt.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        maleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.haveMale.value = isChecked // Update the haveMale value in SharedViewModel
        }

        femaleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.haveFemale.value = isChecked
        }

        babySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.haveBaby.value = isChecked
        }

        prayerSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.havePrayer.value = isChecked
        }

        disabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.haveDisabled.value = isChecked
        }
    }

    fun goBack(view: View) {
        finish()
    }
}