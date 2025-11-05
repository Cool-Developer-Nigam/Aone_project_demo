package com.nigdroid.aone_project.ui.students

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nigdroid.aone_project.R
import com.nigdroid.aone_project.data.model.StudentResponse
import com.nigdroid.aone_project.databinding.ItemStudentBinding

class StudentAdapter(
    private val onEditClick: (StudentResponse) -> Unit,
    private val onDeleteClick: (StudentResponse) -> Unit,
    private val onItemClick: (StudentResponse) -> Unit
) : ListAdapter<StudentResponse, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)

        // Animate item on bind
        holder.itemView.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay((position % 10) * 50L)
                .start()
        }
    }

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentResponse) {
            binding.apply {
                // Set student name
                studentName.text = student.name

                // Set avatar initial
                avatarInitial.text = student.name.firstOrNull()?.uppercase() ?: "?"

                // Set roll number
                rollNumber.text = student.rollNo

                // Set class
                className.text = student.className

                // Set contact
                contactNumber.text = student.contact

                // Set random avatar color
                val colors = listOf(
                    "#667eea", "#764ba2", "#f093fb",
                    "#4facfe", "#43e97b", "#fa709a"
                )
                val colorIndex = student.id % colors.size
                avatarCard.setCardBackgroundColor(
                    android.graphics.Color.parseColor(colors[colorIndex])
                )

                // Click listeners with animations
                root.setOnClickListener {
                    animateClick(it)
                    onItemClick(student)
                }

                editButton.setOnClickListener {
                    animateButton(it)
                    onEditClick(student)
                }

                deleteButton.setOnClickListener {
                    animateButton(it)
                    onDeleteClick(student)
                }
            }
        }

        private fun animateClick(view: android.view.View) {
            view.animate()
                .scaleX(0.98f)
                .scaleY(0.98f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }

        private fun animateButton(view: android.view.View) {
            val rotation = android.animation.ObjectAnimator.ofFloat(
                view, "rotation", 0f, 360f
            )
            rotation.duration = 300
            rotation.start()
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<StudentResponse>() {
        override fun areItemsTheSame(
            oldItem: StudentResponse,
            newItem: StudentResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StudentResponse,
            newItem: StudentResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}