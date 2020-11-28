package io.pacworx.ambrosia.achievements

import javax.persistence.*

@Entity
class TaskCluster(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @Enumerated(EnumType.STRING)
    val category: TaskCategory,
    val sortOrder: Int
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "task_cluster_id")
    @OrderBy("number ASC")
    var tasks: List<Task> = ArrayList()
}