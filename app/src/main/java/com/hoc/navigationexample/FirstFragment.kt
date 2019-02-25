package com.hoc.navigationexample

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.grid_item.view.*
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.horizontal_recycler.view.*
import kotlinx.android.synthetic.main.horz_item.view.*
import kotlin.random.Random

sealed class Model {
  data class Header(val header: String) : Model()
  data class HorizontalList1(val strings: List<String>) : Model()
  data class HorizontalList2(val strings: List<String>) : Model()
  data class GridItemList(val item: String) : Model()
}

class Adapter :
  ListAdapter<Model, Adapter.VH>(object : DiffUtil.ItemCallback<Model?>() {
    override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
      return when {
        oldItem is Model.Header && newItem is Model.Header -> oldItem.header == newItem.header
        oldItem is Model.HorizontalList1 && newItem is Model.HorizontalList1 -> oldItem.strings == newItem.strings
        oldItem is Model.HorizontalList2 && newItem is Model.HorizontalList2 -> oldItem.strings == newItem.strings
        oldItem is Model.GridItemList && newItem is Model.GridItemList -> oldItem.item == newItem.item
        else -> oldItem == newItem
      }
    }

    override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
      return oldItem == newItem
    }
  }) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    log {
      "onCreateViewHolder: " + when (viewType) {
        R.layout.header -> "VH.HeaderVH"
        R.id.horizontal_recycler1 -> "VH.HorizontalRecyclerVH1"
        R.id.horizontal_recycler2 -> "VH.HorizontalRecyclerVH2"
        R.layout.grid_item -> "VH.GridItemVH"
        else -> "#"
      }
    }

    val view = LayoutInflater.from(parent.context).inflate(
      if (viewType in listOf(R.id.horizontal_recycler1, R.id.horizontal_recycler2)) {
        R.layout.horizontal_recycler
      } else {
        viewType
      }, parent, false
    )
    return when (viewType) {
      R.layout.header -> VH.HeaderVH(view)
      R.id.horizontal_recycler1 -> VH.HorizontalRecyclerVH1(view)
      R.id.horizontal_recycler2 -> VH.HorizontalRecyclerVH2(view)
      R.layout.grid_item -> VH.GridItemVH(view)
      else -> throw java.lang.IllegalStateException("Fucking...")
    }
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is Model.Header -> R.layout.header
      is Model.HorizontalList1 -> R.id.horizontal_recycler1
      is Model.HorizontalList2 -> R.id.horizontal_recycler2
      is Model.GridItemList -> R.layout.grid_item
    }
  }

  sealed class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(model: Model)

    class HeaderVH(itemView: View) : VH(itemView) {
      override fun bind(model: Model) {
        if (model !is Model.Header) {
          throw IllegalStateException("Fucking...")
        }
        log { "HeaderVH::bind model=$model" }
        text.text = model.header
      }

      private val text = itemView.text_header!!

    }

    class HorizontalRecyclerVH1(itemView: View) : VH(itemView) {
      private var currentList: List<String> = emptyList()
      private val recycler = itemView.recycler!!
      private val adapter = HorizontalAdapter().apply {
        submitList(this@HorizontalRecyclerVH1.currentList)
      }


      init {
        recycler.run {
          adapter = this@HorizontalRecyclerVH1.adapter
          setHasFixedSize(true)
          layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
      }

      override fun bind(model: Model) {
        if (model !is Model.HorizontalList1) {
          throw IllegalStateException("Fucking...")
        }
        if (currentList != model.strings) {
          adapter.submitList(model.strings.also { currentList = it })
          log { "HorizontalRecyclerVH::bind model=$model" }
        }
      }

    }

    class HorizontalRecyclerVH2(itemView: View) : VH(itemView) {
      private var currentList: List<String> = emptyList()
      private val recycler = itemView.recycler!!
      private val adapter = HorizontalAdapter().apply {
        submitList(this@HorizontalRecyclerVH2.currentList)
      }


      init {
        recycler.run {
          adapter = this@HorizontalRecyclerVH2.adapter
          setHasFixedSize(true)
          layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
      }

      override fun bind(model: Model) {
        if (model !is Model.HorizontalList2) {
          throw IllegalStateException("Fucking...")
        }
        if (currentList != model.strings) {
          adapter.submitList(model.strings.also { currentList = it })
          log { "HorizontalRecyclerVH::bind model=$model" }
        }
      }

    }

    class GridItemVH(itemView: View) : VH(itemView) {
      override fun bind(model: Model) {
        if (model !is Model.GridItemList) {
          throw IllegalStateException("Fucking...")
        }
        log { "GridItemVH::bind model=$model" }
        text.text = model.item
      }

      private val text = itemView.text
    }
  }
}

class FirstFragment : Fragment() {
  private val adapter = Adapter()


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.fragment_first, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recycler.run {
      setHasFixedSize(true)

      adapter = this@FirstFragment.adapter.apply {
        submitList(
          listOf(
            Model.Header("HEADER 1"),
            Model.HorizontalList1(List(120) { "Horizontal list item $it" }),
            Model.Header("HEADER 2"),
            Model.HorizontalList2(List(120) { "Horizontal list item ${120 - it}" }),
            Model.Header("HEADER 3")
          ) + List(300) { Model.GridItemList("Grid item $it") }
        )
      }
      layoutManager = GridLayoutManager(context, 2).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
          override fun getSpanSize(position: Int) =
            if (adapter!!.getItemViewType(position) == R.layout.grid_item) 1 else 2
        }
      }
    }

    Handler().postDelayed(
      {
        adapter.submitList(
          adapter.currentList.map {
            if (it is Model.HorizontalList1) {
              Model.HorizontalList1(List(120) { "Horizontal list item ${Random.nextInt(it + 1)}" })
            } else {
              it
            }
          }
        ) { log { "FirstFragment::update..." } }
      },
      20_000
    )

    log { "FirstFragment::onViewCreated" }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    log { "FirstFragment::onDestroyView" }
  }
}

class HorizontalAdapter :
  ListAdapter<String, HorizontalAdapter.VH>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }
  }) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return LayoutInflater.from(parent.context).inflate(R.layout.horz_item, parent, false).let(::VH)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.bind(getItem(position))
  }

  class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String) {
      text_horz.text = item
    }

    private val text_horz = itemView.text_horz
  }
}