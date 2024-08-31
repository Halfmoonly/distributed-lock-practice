package org.lyflexi.jvmlock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class JvmlockApplicationTests {

    @Test
    void contextLoads() {
    }




//    private List<Integer> solution(TreeNode root){
//        if (root == null){
//            return new ArrayList<>();
//        }
//        ArrayList<Integer> answer = new ArrayList<>();
//        LinkedList<TreeNode> queue = new LinkedList<>();
//        queue.offer(root);
//        while (!queue.isEmpty()){
//
//            int n = queue.size();
//            for (int i = 0; i < n; i++) {
//                TreeNode node = queue.poll();
//                if (node.left!=null){
//                    queue.offer(node.left);
//                }
//                if (node.right!=null){
//                    queue.offer(node.right);
//                }
//                if (i==n-1){
//                    answer.add(node.var);
//                }
//            }
//        }
//        return answer;
//    }

}
