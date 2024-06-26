package org.piramalswasthya.cho.ui.register_patient_activity

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import org.piramalswasthya.cho.R
import org.piramalswasthya.cho.databinding.FragmentRegisterPatientBinding
import org.piramalswasthya.cho.ui.commons.NavigationAdapter

class RegisterPatientFragment : Fragment() {

    private var _binding: FragmentRegisterPatientBinding? = null
    private val binding: FragmentRegisterPatientBinding
        get() = _binding!!

    private lateinit var currFragment: NavigationAdapter

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
//            title = resources.getString(R.string.title_patient_list)
            title = Html.fromHtml("<font color='#FFFFFF'>Personal Details</font>")
            setDisplayHomeAsUpEnabled(true)
        }
        navHostFragment =
            childFragmentManager.findFragmentById(binding.patientRegistration.id) as NavHostFragment

        binding.btnSubmit.setOnClickListener {
            currFragment =
                navHostFragment.childFragmentManager.primaryNavigationFragment as NavigationAdapter

            when (currFragment.getFragmentId()) {
                R.id.fragment_fhir_add_patient -> {
                    binding.headerTextRegisterPatient.text =
                        resources.getString(R.string.location_details)
                    binding.btnSubmit.text = resources.getString(R.string.next)
                    binding.btnCancel.text = resources.getString(R.string.cancel)
                }

                R.id.fragment_fhir_add_patient_location -> {
                    binding.headerTextRegisterPatient.text =
                        resources.getString(R.string.other_info)
                    binding.btnSubmit.text = resources.getString(R.string.submit)
                    binding.btnCancel.text = resources.getString(R.string.cancel)
                }
                R.id.fragment_fhir_add_patient_other_details ->{

                }

            }

            currFragment.onSubmitAction()
        }


        binding.btnCancel.setOnClickListener {

            currFragment = navHostFragment.childFragmentManager.primaryNavigationFragment as NavigationAdapter

            when (currFragment.getFragmentId()){
                R.id.fragment_fhir_add_patient -> {

                }

                R.id.fragment_fhir_add_patient_location -> {
                    binding.headerTextRegisterPatient.text =
                        resources.getString(R.string.personal_details)
                    binding.btnSubmit.text = resources.getString(R.string.next)
                    binding.btnCancel.text = resources.getString(R.string.cancel)
                }
                R.id.fragment_fhir_add_patient_other_details ->{
                    binding.headerTextRegisterPatient.text =
                        resources.getString(R.string.location_details)
                    binding.btnSubmit.text = resources.getString(R.string.next)
                    binding.btnCancel.text = resources.getString(R.string.cancel)
                }
            }

            currFragment.onCancelAction()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}