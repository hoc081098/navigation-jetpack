package com.hoc.navigationexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_second.*


class SecondFragment : Fragment() {
  private val args by navArgs<SecondFragmentArgs>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.fragment_second, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    log { "SecondFragment::onViewCreated" }

    text_email.text = args.user.email
    text_name.text = args.user.name
  }

  override fun onDestroyView() {
    super.onDestroyView()
    log { "SecondFragment::onDestroyView" }
  }
}