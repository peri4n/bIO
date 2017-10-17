# Algorithms

This page some of the Whys and Hows of our application. It is not only used as
documentation for the interested user but also as a way to contemplate our own
thoughts.

## Overall picture

Every search engine has two modes of working. The obvious one being _Searching_
and the other being _Indexing_. While indexing is not often most be users, its
implications might have an impact on some power users.

### Indexing

We index all incoming data in a _Lucene_ index. This index allows users to
query which already indexed documents match a given search query. With
biological sequences being sometimes very large this poses a problem. The sheer
fact that your query matches against a sequence with the length of a million is
not very meaningful. The user would much rather have the information where the
query matched. That is why we split the sequences.

#### Splitting

Splitting our sequences allows us to retieve small fragments that matched
against our query and see at where the matched occured. This is efficient
because the length of the sequence is so small that its performance will not
effect user experience.

The process has several parameters: _Fragment length_ and _Overlap_.

Fragment length controls the final length of the fragment that is indexed.
Overlap is the size of overlap of neighbouring fragments. So overlap can not be
bigger than the fragment length.

### Searching

When we query the index, we get a list of documents that matched our query.
This should be merged so that fragments coming from the same sequece do not
appear twice. This list allows the UI to present all matching sequences to the
user.

When clicking on a match the matching fragments are retrieved and possibly
merged. This partial reconstruction of the original sequence is then send to
the front end to generate an alignment.

Note, this approach defers the computation heavy steps as much as possible so
they are only executed when the user really needs them. This approach is often
called _lazy evaluation_. Also, the actual alignment is carried out at the
users browser. We believe that modern computers have no problem with this and
it allows us to scale almost independently from user numbers and sequence size.
