package com.hoc.navigationexample

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.fragment_home.*

inline fun log(message: () -> String) = Log.d("@@@", message())

data class User(val name: String, val email: String) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readString()!!,
    parcel.readString()!!
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(name)
    parcel.writeString(email)
  }

  override fun describeContents(): Int = 0

  companion object CREATOR : Parcelable.Creator<User> {
    override fun createFromParcel(parcel: Parcel): User = User(parcel)
    override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
  }
}

class HomeFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.fragment_home, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    log { "HomeFragment::onViewCreated" }

    val users = List(120) { User("Hoc$it", "hoc081098_$it@gmail") }
    val user = users.random()

    text_name.text = user.name
    text_email.text = user.email

    button_nav_to_second.setOnClickListener { v ->
      HomeFragmentDirections.actionHomeFragmentToSecondFragment(user).let {
        v.findNavController().navigate(it)
      }
    }

    recycler_view.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(context)
      adapter = UserAdapter(users) {
        HomeFragmentDirections.actionHomeFragmentToSecondFragment(it).let {
          findNavController().navigate(it)
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    log { "HomeFragment::onDestroyView" }
  }
}

class UserAdapter(private val users: List<User>, private val onClickListener: (User) -> Unit) :
  RecyclerView.Adapter<UserAdapter.VH>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    LayoutInflater.from(parent.context).inflate(
      android.R.layout.simple_expandable_list_item_2,
      parent,
      false
    ).let(::VH)

  override fun getItemCount() = users.size

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(users[position])

  inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val text1 = itemView.findViewById<TextView>(android.R.id.text1)!!
    private val text2 = itemView.findViewById<TextView>(android.R.id.text2)!!

    init {
      itemView.setOnClickListener {
        val position = adapterPosition
        if (position != NO_POSITION) {
          onClickListener(users[position])
        }
      }
    }

    fun bind(user: User) {
      text1.text = user.name
      text2.text = user.email
    }
  }
}