function isSym(root) {
  return isSymmetrical(root, root);
}

function isSymmetrical(root1, root2) {
  if (root1 == null && root2 == null) return true;
  if (root1 == null || root2 == null) return false;
  if (root1.value == root2.value) return true;
  return isSymmetrical(root1.left, root2.right) && isSymmetrical(root1.right, root2.left);
}